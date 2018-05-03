package endpoints.xhr.faithful

import endpoints.algebra.{Decoder, Encoder, MuxRequest}
import endpoints.xhr
import faithful.{Future, Promise}

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
    ): Future[req.Response] = {
      val promise = new Promise[req.Response]()
      muxPerformXhr(request, response, req)(
        _.fold(promise.failure, promise.success),
        xhr => promise.failure(new Exception(xhr.responseText))
      )
      promise.future
    }
  }

  def muxEndpoint[Req <: MuxRequest, Resp, ReqTransport, RespTransport](
    request: Request[ReqTransport],
    response: Response[RespTransport]
  ): MuxEndpoint[Req, Resp, ReqTransport, RespTransport] =
    new MuxEndpoint[Req, Resp, ReqTransport, RespTransport](request, response)

}
