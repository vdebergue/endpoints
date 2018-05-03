package endpoints.play.server

import endpoints.algebra
import endpoints.algebra.{Decoder, Encoder, MuxRequest}
import play.api.mvc.Result

import scala.concurrent.Future

trait MuxEndpoints extends algebra.MuxEndpoints with Endpoints {

  import playComponents.executionContext

  class MuxEndpoint[Req <: MuxRequest, Resp, ReqTransport, RespTransport](
    request: Request[ReqTransport],
    response: Response[RespTransport]
  ) {
    def implementedBy(
      handler: MuxHandler[Req, Resp]
    )(implicit
      decoder: Decoder[ReqTransport, Req],
      encoder: Encoder[Resp, RespTransport]
    ): ToPlayHandler =
      toPlayHandler(req => Future.successful(handler(req)))

    def implementedByAsync(
      handler: MuxHandlerAsync[Req, Resp]
    )(implicit
      decoder: Decoder[ReqTransport, Req],
      encoder: Encoder[Resp, RespTransport]
    ): ToPlayHandler =
      toPlayHandler(req => handler(req))

    def toPlayHandler(
      handler: Req { type Response = Resp } => Future[Resp]
    )(implicit
      decoder: Decoder[ReqTransport, Req],
      encoder: Encoder[Resp, RespTransport]
    ): ToPlayHandler =
      header =>
        request.decode(header).map { bodyParser =>
          playComponents.actionBuilder.async(bodyParser) { request =>
            handler(decoder.decode(request.body).right.get /* TODO Handle failure */.asInstanceOf[Req { type Response = Resp}])
              .map(resp => response(encoder.encode(resp)))
          }
        }
  }

  def muxEndpoint[Req <: MuxRequest, Resp, ReqTransport, RespTransport](
    request: Request[ReqTransport],
    response: RespTransport => Result
  ): MuxEndpoint[Req, Resp, ReqTransport, RespTransport] =
    new MuxEndpoint[Req, Resp, ReqTransport, RespTransport](request, response)

}


//#mux-handler-async
/**
  * A function whose return type depends on the type
  * of the given `req`.
  *
  * @tparam Req Request base type
  * @tparam Resp Response base type
  */
trait MuxHandlerAsync[Req <: MuxRequest, Resp] {
  def apply[R <: Resp](req: Req { type Response = R }): Future[R]
}
//#mux-handler-async

/**
  * A function whose return type depends on the type
  * of the given `req`.
  *
  * @tparam Req Request base type
  * @tparam Resp Response base type
  */
trait MuxHandler[Req <: MuxRequest, Resp] {
  def apply[R <: Resp](req: Req { type Response = R }): R
}
