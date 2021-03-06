package io.finch.syntax

import com.twitter.finagle.http.Method
import io.finch._

class EndpointMapper[A](m: Method, e: Endpoint[A]) extends Endpoint[A] { self =>

  /**
   * Maps this endpoint to either `A => Output[B]` or `A => Future[Output[B]]`.
   */
  final def apply(mapper: Mapper[A]): Endpoint[mapper.Out] = mapper(self)

  final def apply(input: Input): Endpoint.Result[A] =
      if (input.request.method == m) e(input)
      else e(input) match {
        case EndpointResult.Matched(_, _) => EndpointResult.NotMatched.MethodNotAllowed(m :: Nil)
        case skipped => skipped
      }

  final override def toString: String = s"${ m.toString.toUpperCase } /${ e.toString }"
}
