package endpoints.xhr.thenable

import endpoints.algebra.{Decoder, Encoder, MuxRequest}
import endpoints.xhr

import scala.scalajs.js

trait MuxEndpoints extends xhr.MuxEndpoints with Endpoints {

  class MuxEndpoint[Req <: MuxRequest, Resp, ReqTransport, RespTransport](
    request: Request[ReqTransport],
    response: Response[RespTransport]
  ) {
    def apply(
      req: Req
    )(implicit
      encoder: Encoder[Req, ReqTransport],
      decoder: Decoder[RespTransport, Resp]
    ): js.Thenable[req.Response] = {
      new js.Promise[req.Response]((resolve, error) => {
        muxPerformXhr(request, response, req)(
          _.fold(exn => error(exn.getMessage), resp => resolve(resp)),
          xhr => error(xhr.responseText)
        )
      })
    }
  }

  def muxEndpoint[Req <: MuxRequest, Resp, ReqTransport, RespTransport](
    request: Request[ReqTransport],
    response: Response[RespTransport]
  ): MuxEndpoint[Req, Resp, ReqTransport, RespTransport] =
    new MuxEndpoint[Req, Resp, ReqTransport, RespTransport](request, response)

}
