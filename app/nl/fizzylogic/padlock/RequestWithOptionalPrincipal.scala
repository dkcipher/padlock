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

import play.api.mvc.{Request, WrappedRequest}

/**
  * Companion object for RequestWithOptionalPrincipal
  */
object RequestWithOptionalPrincipal {
  /**
    * Creates a new request with a principal
    *
    * @param principal optional principal to attach to the request
    * @param request   original request
    * @tparam A body content type
    * @return The new instance of RequestWithOptionalPrincipal
    */
  def apply[A](principal: Option[Principal], request: Request[A]): RequestWithOptionalPrincipal[A] = {
    new RequestWithOptionalPrincipal[A](principal, request)
  }
}

/**
  * A request with an optional principal
  *
  * @param principal optional principal for the request
  * @param request   original request
  */
class RequestWithOptionalPrincipal[A](val principal: Option[Principal], request: Request[A]) extends WrappedRequest[A](request) {

}
