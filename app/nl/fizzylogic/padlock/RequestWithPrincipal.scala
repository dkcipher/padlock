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
  * Companion object for RequestWithPrincipal
  */
object RequestWithPrincipal {
  /**
    * Creates a new request with a principal
    *
    * @param principal principal to attach
    * @param request   original request
    * @tparam A body content type
    * @return the new instance of RequestWithPrincipal
    */
  def apply[A](principal: Principal, request: Request[A]): RequestWithPrincipal[A] = {
    new RequestWithPrincipal(principal, request)
  }
}

/**
  * A request with a principal attached to it
  *
  * @param principal principal object for the current user
  * @param request   original request
  * @tparam A body content type
  */
class RequestWithPrincipal[A](val principal: Principal, request: Request[A]) extends WrappedRequest[A](request) {

}
