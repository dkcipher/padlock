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

import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Adds authorization support to a controller
  */
trait AuthorizationSupport extends Controller {
  /**
    * The authorization handler that is used to handle various bits of authorization logic
    * that cannot be handled by the authorization support component directly.
    */
  def authorizationHandler: AuthorizationHandler

  /**
    * Creates an action that requires an authenticated user
    */
  object AuthenticatedAction extends ActionBuilder[RequestWithPrincipal] {
    /**
      * Invokes the provided code block with a valid principal if the user is authenticated.
      * Otherwise invokes the unauthorized method on the authorization handler instead.
      *
      * @param request incoming request
      * @param block   code block to execute
      * @tparam A body content type
      * @return the result of the action
      */
    override def invokeBlock[A](request: Request[A], block: (RequestWithPrincipal[A]) => Future[Result]): Future[Result] = {

      def unauthorizedAction = authorizationHandler.unauthorized(RequestWithOptionalPrincipal(None, request))

      def authorizedAction(principal: Principal) = block(RequestWithPrincipal(principal, request))

      authorizationHandler.principal(request).fold(unauthorizedAction)(authorizedAction)
    }
  }

  /**
    * Creates an action with an optional principal
    */
  object ActionWithOptionalPrincipal extends ActionBuilder[RequestWithOptionalPrincipal] {
    /**
      * Invokes the provided code block with an optional principal.
      *
      * @param request incoming request
      * @param block   code block to execute
      * @tparam A body content type
      * @return the result of the action
      */
    override def invokeBlock[A](request: Request[A], block: (RequestWithOptionalPrincipal[A]) => Future[Result]): Future[Result] = {
      block(RequestWithOptionalPrincipal(authorizationHandler.principal(request), request))
    }
  }

  /**
    * Authorizes the request using an authorization policy
    *
    * @param policy policy that you want to use to authorize the action
    * @tparam A type of body content
    * @return The authorization policy action refiner
    */
  def authorizeUsingPolicy[A](policy: AuthorizationPolicy): ActionRefiner[RequestWithPrincipal, RequestWithPrincipal] =
    new ActionRefiner[RequestWithPrincipal, RequestWithPrincipal] {
      override protected def refine[A](request: RequestWithPrincipal[A]): Future[Either[Result, RequestWithPrincipal[A]]] = {
        if (!policy.allowed(request)) {
          authorizationHandler.denied(request).map(Left(_))
        } else {
          Future.successful(Right(request))
        }
      }
    }

//  /**
//    * Type alias for RequestWithPrincipalAndResource
//    *
//    * @tparam A body content type
//    * @tparam R resource type
//    */
//  type ReqWithPrAndRes[A, R] = RequestWithPrincipalAndResource[A, R]
//
//  /**
//    * Authorizes a request for a resource using a resource authorization policy
//    *
//    * @param policy policy to use for authorizing the request
//    * @tparam A body content type
//    * @tparam R resource type
//    * @return The authorization policy action refiner
//    */
//  def authorizeUsingResourceAuthorizationPolicy[A, R](policy: ResourceAuthorizationPolicy[R]): ActionRefiner[ReqWithPrAndRes, ReqWithPrAndRes] =
//    new ActionRefiner[ReqWithPrAndRes, ReqWithPrAndRes] {
//      override protected def refine[B](request: ReqWithPrAndRes[B, R]): Future[Either[Result, ReqWithPrAndRes[B, R]]] = {
//        if (!policy.allowed(request.resource, request)) {
//          authorizationHandler.denied(request).map(Left(_))
//        } else {
//          Future.successful(Right(request))
//        }
//      }
//    }
}
