package endpoints.documented.openapi

import endpoints.algebra.MuxRequest
import endpoints.documented.algebra

trait MuxEndpoints extends algebra.MuxEndpoints with Endpoints {

  type MuxEndpoint[Req <: MuxRequest, Resp,  ReqTransport, RespTransport] = DocumentedEndpoint

  def muxEndpoint[Req <: MuxRequest, Resp,  ReqTransport, RespTransport](
    request: Request[ReqTransport],
    response: Response[RespTransport]
  ): MuxEndpoint[Req, Resp,  ReqTransport, RespTransport] = endpoint(request, response)

}