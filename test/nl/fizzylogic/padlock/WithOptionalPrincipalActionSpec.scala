// Copyright (C) 2011-2012 the original author or authors.
// See the LICENCE file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


package nl.fizzylogic.padlock

import akka.stream.Materializer
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.mvc.Results._
import play.api.mvc.{Controller, Request}
import play.api.test.{FakeRequest}
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WithOptionalPrincipalActionSpec extends PlaySpec with OneAppPerSuite {
  implicit lazy val materializer: Materializer = app.materializer

  var currentPrincipal: Option[Principal] = None

  val controller = new Controller with AuthorizationSupport {
    /**
      * The authorization handler that is used to handle various bits of authorization logic
      * that cannot be handled by the authorization support component directly.
      */
    override def authorizationHandler = new AuthorizationHandler {
      /**
        * Handles a denied request
        *
        * @param request request that was rejected by the authorization component
        * @tparam A body content type
        * @return the result to render instead of the original result
        */
      override def denied[A](request: RequestWithPrincipal[A]) = Future {
        Forbidden("")
      }

      /**
        * handles an unauthorized request
        *
        * @param request request that was rejected by the authorization component
        * @tparam A body content type
        * @return the result to render instead of the original result
        */
      override def unauthorized[A](request: RequestWithOptionalPrincipal[A]) = Future {
        Unauthorized("")
      }

      /**
        * Determines the principal for the request
        *
        * @param request incoming request
        * @tparam A body content type
        * @return the optional principal
        */
      override def principal[A](request: Request[A]): Option[Principal] = currentPrincipal
    }
  }

  val action = controller.ActionWithOptionalPrincipal { implicit request =>
    Ok("").withHeaders(("X-Principal-Name", request.principal.map(x => x.identifier).getOrElse("")))
  }

  "AuthorizationSupport#ActionWithOptionalPrincipal" should {
    "Create a request with an optional principal" in {
      currentPrincipal = Some(new Principal {
        /**
          * The identifying name of the principal
          */
        override def identifier: String = "some@domain.org"

        /**
          * The claims associated with the principal
          */
        override def claims: Seq[Nothing] = Seq.empty
      })

      val result = call(action, FakeRequest("GET", "/"))

      header("X-Principal-Name", result) mustBe Some("some@domain.org")
    }
  }
}
