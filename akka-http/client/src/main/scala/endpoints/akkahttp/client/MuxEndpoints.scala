package endpoints
package akkahttp.client

import endpoints.algebra.{Decoder, Encoder, MuxRequest}

import scala.concurrent.Future

trait MuxEndpoints extends algebra.MuxEndpoints { self: Endpoints =>


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
      request(encoder.encode(req)).flatMap { resp =>
        response(resp).flatMap { t =>
          futureFromEither(t).flatMap(tt =>
            futureFromEither(decoder.decode(tt)).map(_.asInstanceOf[req.Response])
          )
        }
      }
  }

  def muxEndpoint[Req <: MuxRequest, Resp, ReqTransport, RespTransport](
    request: Request[ReqTransport],
    response: Response[RespTransport]
  ): MuxEndpoint[Req, Resp, ReqTransport, RespTransport] =
    new MuxEndpoint[Req, Resp, ReqTransport, RespTransport](request, response)

}
