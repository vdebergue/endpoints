package endpoints.documented.delegate

import endpoints.algebra.MuxRequest

trait MuxEndpoints extends endpoints.documented.algebra.MuxEndpoints with Endpoints {

  val delegate: endpoints.algebra.MuxEndpoints

  type MuxEndpoint[Req <: MuxRequest, Resp, ReqTransport, RespTransport] = delegate.MuxEndpoint[Req, Resp, ReqTransport, RespTransport]

  def muxEndpoint[Req <: MuxRequest, Resp, ReqTransport, RespTransport](
    request: Request[ReqTransport],
    response: Response[RespTransport]
  ): MuxEndpoint[Req, Resp,  ReqTransport, RespTransport] = delegate.muxEndpoint(request, response)

}
