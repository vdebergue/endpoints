package endpoints.testsuite

import endpoints.algebra
import endpoints.algebra.MuxRequest
import io.circe.Json

trait MuxEndpointTestApi extends algebra.MuxEndpoints with algebra.circe.JsonEntitiesFromCodec {

  sealed trait QueryReq extends MuxRequest
  final case class FindById(id: String) extends QueryReq {
    override type Response = MaybeRessource
  }
  final case object FindAll extends QueryReq {
    override type Response = ResourceList
  }

  sealed trait QueryResp
  case class MaybeRessource(value: Option[String]) extends QueryResp
  case class ResourceList(value: List[String]) extends QueryResp

  val query: MuxEndpoint[QueryReq, QueryResp, Option[String], Json] = {
    val request: Request[Option[String]] = get(path / "query" /? optQs[String]("id"))
    muxEndpoint(request, jsonResponse)
  }
}
