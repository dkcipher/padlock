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
import play.api.mvc.{Controller, Request, Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class AuthorizationPolicySpec extends PlaySpec with OneAppPerSuite with Results {
  "AuthorizationSupport#authorizeWithPolicy" should {

    implicit val mat: Materializer = app.materializer

    var currentPrincipal: Option[Principal] = Some(new Principal {
      /**
        * The identifying name of the principal
        */
      override def identifier: String = "some@domain.org"

      /**
        * The claims associated with the principal
        */
      override def claims: Seq[Claim] = Seq.empty
    })

    val controller = new Controller with AuthorizationSupport {
      /**
        * The authorization handler that is used to handle various bits of authorization logic
        * that cannot be handled by the authorization support component directly.
        */
      override def authorizationHandler: AuthorizationHandler = new AuthorizationHandler {/**
        * Handles a denied request
        *
        * @param request request that was rejected by the authorization component
        * @tparam A body content type
        * @return the result to render instead of the original result
        */
      override def denied[A](request: RequestWithPrincipal[A]): Future[Result] = {
        Future.successful(Forbidden(""))
      }

        /**
          * handles an unauthorized request
          *
          * @param request request that was rejected by the authorization component
          * @tparam A body content type
          * @return the result to render instead of the original result
          */
        override def unauthorized[A](request: RequestWithOptionalPrincipal[A]): Future[Result] = {
          Future.successful(Unauthorized(""))
        }

        /**
          * Determines the principal for the request
          *
          * @param request incoming request
          * @tparam A body content type
          * @return the optional principal
          */
        override def principal[A](request: Request[A]): Option[Principal] = {
          currentPrincipal
        }
      }
    }



    "allow authorized users" in {
      val policy = new AuthorizationPolicy {
        /**
          * Checks if access is allowed based on the request
          *
          * @param request request to authorize
          */
        override def allowed[A](request: RequestWithPrincipal[A]) = true
      }

      val action = (controller.AuthenticatedAction andThen controller.authorizeUsingPolicy(policy)) {
        Ok("")
      }

      val result = call(action, FakeRequest("GET","/"))

      status(result) mustBe 200
    }

    "deny unauthorized users" in {
      val policy = new AuthorizationPolicy {
        /**
          * Checks if access is allowed based on the request
          *
          * @param request request to authorize
          */
        override def allowed[A](request: RequestWithPrincipal[A]) = false
      }

      val action = (controller.AuthenticatedAction andThen controller.authorizeUsingPolicy(policy)) {
        Ok("")
      }

      val result = call(action, FakeRequest("GET","/"))

      status(result) mustBe 403
    }
  }
}
