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

import play.api.mvc.{Request, Result}

import scala.concurrent.Future

/**
  * Implement this trait to provide logic for specific authorization related logic
  */
trait AuthorizationHandler {
  /**
    * Handles a denied request
    *
    * @param request request that was rejected by the authorization component
    * @tparam A body content type
    * @return the result to render instead of the original result
    */
  def denied[A](request: RequestWithPrincipal[A]): Future[Result]

  /**
    * handles an unauthorized request
    *
    * @param request request that was rejected by the authorization component
    * @tparam A body content type
    * @return the result to render instead of the original result
    */
  def unauthorized[A](request: RequestWithOptionalPrincipal[A]): Future[Result]

  /**
    * Determines the principal for the request
    *
    * @param request incoming request
    * @tparam A body content type
    * @return the optional principal
    */
  def principal[A](request: Request[A]): Option[Principal]
}
