package endpoints
package akkahttp.server

import akka.http.scaladsl.server.{Directives, Route}
import endpoints.algebra.{Decoder, Encoder, MuxRequest}

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Extends the [[Endpoints]] interpreter with [[algebra.MuxEndpoints]]
  * support.
  */
trait MuxEndpoints extends algebra.MuxEndpoints with Endpoints {

  class MuxEndpoint[Req <: MuxRequest, Resp, ReqTransport, RespTransport](request: Request[ReqTransport], response: Response[RespTransport]) {

    def implementedBy(handler: MuxHandler[Req, Resp])(implicit
      decoder: Decoder[ReqTransport, Req],
      encoder: Encoder[Resp, RespTransport]
    ): Route = handleAsync(req => Future.successful(handler(req)))

    def implementedByAsync(handler: MuxHandlerAsync[Req, Resp])(implicit
      decoder: Decoder[ReqTransport, Req],
      encoder: Encoder[Resp, RespTransport]
    ): Route = handleAsync(req => handler(req))

    private def handleAsync(handler: Req {type Response = Resp} => Future[Resp])(implicit
      decoder: Decoder[ReqTransport, Req],
      encoder: Encoder[Resp, RespTransport]
    ): Route =
      request { request =>
        Directives.onComplete(handler(decoder.decode(request).right.get /* TODO Handle failure */ .asInstanceOf[Req {type Response = Resp}])) {
          case Success(result) => response(encoder.encode(result))
          case Failure(ex) => Directives.complete(ex)
        }
      }

  }

  def muxEndpoint[Req <: MuxRequest, Resp, ReqTransport, RespTransport](
    request: Request[ReqTransport],
    response: Response[RespTransport]
  ): MuxEndpoint[Req, Resp, ReqTransport, RespTransport] =
    new MuxEndpoint[Req, Resp, ReqTransport, RespTransport](request, response)

}

/**
  * A function whose return type depends on the type
  * of the given `req`.
  *
  * @tparam Req  Request base type
  * @tparam Resp Response base type
  */
trait MuxHandlerAsync[Req <: MuxRequest, Resp] {
  def apply[R <: Resp](req: Req { type Response = R }): Future[R]
}

/**
  * A function whose return type depends on the type
  * of the given `req`.
  *
  * @tparam Req  Request base type
  * @tparam Resp Response base type
  */
trait MuxHandler[Req <: MuxRequest, Resp] {
  def apply[R <: Resp](req: Req { type Response = R }): R
}
