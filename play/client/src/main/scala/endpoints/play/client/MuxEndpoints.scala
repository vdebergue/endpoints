package endpoints
package play.client

import endpoints.algebra.{Decoder, Encoder, MuxRequest}
import endpoints.play.client.Endpoints.futureFromEither

import scala.concurrent.Future

trait MuxEndpoints extends algebra.Endpoints { self: Endpoints =>

  class MuxEndpoint[Req <: algebra.MuxRequest, Resp, ReqTransport, RespTransport](
    request: Request[ReqTransport],
    response: Response[RespTransport]
  ) {
    def apply(
      req: Req
    )(implicit
      encoder: Encoder[Req, ReqTransport],
      decoder: Decoder[RespTransport, Resp]
    ): Future[req.Response] =
      request(encoder.encode(req)).flatMap { wsResponse =>
        futureFromEither(response(wsResponse).right.flatMap { t =>
          decoder.decode(t).asInstanceOf[Either[Throwable, req.Response]]
        })
      }
  }

  def muxEndpoint[Req <: MuxRequest, Resp, ReqTransport, RespTransport](
    request: Request[ReqTransport],
    response: Response[RespTransport]
  ): MuxEndpoint[Req, Resp, ReqTransport, RespTransport] =
    new MuxEndpoint[Req, Resp, ReqTransport, RespTransport](request, response)

}
