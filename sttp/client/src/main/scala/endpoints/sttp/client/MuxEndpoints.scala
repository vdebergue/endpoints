package endpoints
package sttp.client

import com.softwaremill.sttp
import endpoints.algebra.{Decoder, Encoder, MuxRequest}

import scala.language.higherKinds

trait MuxEndpoints[R[_]] extends algebra.Endpoints { self: Endpoints[R] =>

  class MuxEndpoint[Req <: algebra.MuxRequest, Resp, ReqTransport, RespTransport](request: Request[ReqTransport], response: Response[RespTransport]) {

    def apply(req: Req)(implicit encoder: Encoder[Req, ReqTransport], decoder: Decoder[RespTransport, Resp]): R[req.Response] = {
      val sttpRequest: sttp.Request[response.ReceivedBody, Nothing] = request(encoder.encode(req)).response(response.responseAs)
      val result = self.backend.send(sttpRequest)
      self.backend.responseMonad.flatMap(result) { res =>
        val transportR: R[RespTransport] = response.validateResponse(res)
        self.backend.responseMonad.flatMap(transportR) { transport =>
          decoder.decode(transport) match {
            case Right(r) => self.backend.responseMonad.unit(r.asInstanceOf[req.Response])
            case Left(exception) => self.backend.responseMonad.error(exception)
          }
        }
      }
    }
  }

  def muxEndpoint[Req <: MuxRequest, Resp, ReqTransport, RespTransport](
    request: Request[ReqTransport],
    response: Response[RespTransport]
  ): MuxEndpoint[Req, Resp, ReqTransport, RespTransport] =
    new MuxEndpoint[Req, Resp, ReqTransport, RespTransport](request, response)

}
