package endpoints.algebra

import scala.language.higherKinds

/**
  * Algebra interface for describing endpoints such that one endpoint can
  * handle several types of requests and responses.
  */
trait MuxEndpoints extends Endpoints {

  /**
    * Information carried by a multiplexed HTTP endpoint.
    */
  type MuxEndpoint[Req <: MuxRequest, Resp, ReqTransport, RespTransport]

  /**
    * Multiplexed HTTP endpoint.
    *
    * A multiplexing endpoint makes it possible to use several request
    * and response types in the same HTTP endpoint. In other words, it
    * allows to define several different actions through a singe HTTP
    * endpoint.
    *
    * @param request The request
    * @param response The response
    * @tparam Req The base type of possible requests
    * @tparam Resp The base type of possible responses
    * @tparam ReqTransport The data type used to transport the requests
    * @tparam RespTransport The data type used to transport the responses
    */
  def muxEndpoint[Req <: MuxRequest, Resp, ReqTransport, RespTransport](
    request: Request[ReqTransport],
    response: Response[RespTransport]
  ): MuxEndpoint[Req, Resp, ReqTransport, RespTransport]

}

/**
  * Multiplexed request type
  */
trait MuxRequest {
  type Response
}
