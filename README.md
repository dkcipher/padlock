# Padlock
[![Build Status](https://travis-ci.org/wmeints/padlock.svg?branch=master)](https://travis-ci.org/wmeints/padlock)

An simple to use authorization module for Play 2.5.x
This project allows you to add very basic authorization logic 
to your Play application.

## Getting started
Download the sources from this repository and run

``` shell
sbt publishLocal
```

After the command completes you can add a dependency to your
project using the following lines:

``` scala
libraryDependencies += "nl.fizzylogic" %% "padlock" % "0.1.0-SNAPSHOT"
```

## Authorizing access to actions
To authorize access to an operation you can use one of the
following two action builders.

 - ActionWithOptionalPrincipal
 - AuthenticatedAction
 
```scala
class MyController @Inject() (val authorizationHandler: AuthorizationHandler) 
extends Controller with AuthorizationSupport {
  
  def myAction = AuthenticatedAction { implicit request =>
    Ok("Hello world")
  }
}
``` 

The controller needs access to an AuthorizationHandler implementation.
This implementation looks like this:

```scala
import play.api.mvc.Results._

class MyAuthorizationHandler extends AuthorizationHandler {
  override def denied[A](request: RequestWithPrincipal[A]) = {
    Forbidden(views.html.accessDenied())
  }
  
  override def unauthorized[A](request: RequestWithOptionalPrincipal[A]) = {
    Unauthorized(views.html.login())
  }
  
  override def principal(request: Request[A]) = {
    //TODO: Retrieve a principal from a user store
    
    request.session("userId").map(Some(MyPrincipal(_,seq.empty)))
  }
}
```

The authorization logic relies on a `Principal` implementation to
get information about the current user. You can create your own
implementation of this by implementing the `Principal` trait.

## More complex authorization operations
The basic authorization operations allow you to specify whether
an action should have a principal or that it is optional but accessible
for an operation.

If you want to create more complex authorization operations you can
implement a custom authorization policy by implementing the `AuthorizationPolicy` trait.
 
```scala
import nl.fizzylogic.padlock._

class MyAuthorizationPolicy extends MyAuthorizationPolicy {
  override def allowed[A](request: RequestWithPrincipal[A]) = {
    request.principal.claims.filter(x => x.claimType.equals("SomeType") && x.value.equals("SomeValue")).length > 0
  }
}
```

## Other features that are coming soon
One of my biggest gripes with existing frameworks is that they don't allow
you to authorize access to specific pieces of data. All they do
is provide a way to authorize specific HTTP endpoints. 

My solution doesn't do any better at the moment and I want to change that.
I want you to be able to authorize access to specific blogposts or whatever your
app happens to have as resources.

## Want to contribute?
I'm more than happy to accept pull requests for this project.
Open one up and show your cool ideas!

