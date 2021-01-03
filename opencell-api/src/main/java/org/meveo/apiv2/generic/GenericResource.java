package org.meveo.apiv2.generic;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.json.simple.parser.ParseException;
import org.meveo.apiv2.models.ApiException;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/generic")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface GenericResource {
    @POST
    @Path("/all/{entityName}")
    @Operation(summary = "Generic single endpoint to retrieve paginated records of an entity",
            tags = { "Generic" },
            description = "You need to specify the entity name which is of the plural form. " +
                    "Also, the first character of the entity name is lowercase. If the entity name is a " +
                    "single word, its characters should be written in lowercase, for example *sellers*. " +
                    "If the entity name is a compound word, only the first character of its first single word is " +
                    "lowercase, the first character of remaining words should be written in uppercase, " +
                    "for example *paymentMethods*.\n\n"
                    + "As body, you can specify the configuration of the research. For example, you can define " +
                    "the offset and the limit, or you can sort by a field and define the sort type. "
                    + "For more details, refer to PagingAndFiltering documentation at the following link " +
                    "[https://opencellsoft.atlassian.net/wiki/spaces/docs/pages/396886017/Generic+API#Paging-and-Filtering](https://opencellsoft.atlassian.net/wiki/spaces/docs/pages/396886017/Generic+API#Paging-and-Filtering).",
            responses = {
                    @ApiResponse(responseCode="200", description = "paginated results successfully retrieved with hypermedia links"),
                    @ApiResponse(responseCode = "400", description = "bad request when entityName not well formed or entity unrecognized")
    })
    Response getAll(@Parameter(description = "the entity name", required = true) @PathParam("entityName") String entityName,
                    @Parameter(description = "requestDto carries the wanted fields ex: {genericFields = [code, description]}", required = true) GenericPagingAndFiltering searchConfig);

    @POST
    @Path("/{entityName}/{id}")
    @Operation(summary = "Generic single endpoint to retrieve resources by ID",
            tags = { "Generic" },
            description ="specify the entity name, the record id, and as body, the list of the wanted fields",
            responses = {
                    @ApiResponse(responseCode="200", description = "paginated results successfully retrieved with hypermedia links"),
                    @ApiResponse(responseCode = "404", description = "baseEntityObject not found", content = @Content(schema = @Schema(implementation = ApiException.class))),
                    @ApiResponse(responseCode = "400", description = "bad request when entityName not well formed or entity unrecognized")
    })
    Response get(@Parameter(description = "The entity name", required = true) @PathParam("entityName") String entityName,
                 @Parameter(description = "The id here is the database primary key of the wanted record", required = true) @PathParam("id") Long id,
                 @Parameter(description = "requestDto carries the wanted fields ex: {fields = [code, description]}", required = true) GenericPagingAndFiltering searchConfig);

    @Operation(summary = "Update a resource by giving it's name and Id",
            tags = { "Generic" },
            description ="specify the entity name, the record id, and as body, the list of the fields to update",
            responses = {
                    @ApiResponse(responseCode="200", description = "resource successfully updated but not content exposed except the hypermedia"),
                    @ApiResponse(responseCode = "404", description = "baseEntityObject not found", content = @Content(schema = @Schema(implementation = ApiException.class))),
                    @ApiResponse(responseCode = "400", description = "bad request when input not well formed")
    })
    @PUT
    @Path("/{entityName}/{id}")
    Response update(@Parameter(description = "the entity name", required = true) @PathParam("entityName") String entityName,
                    @Parameter(description = "The id here is the database primary key of the record to update", required = true) @PathParam("id") Long id,
                    @Parameter(description = "dto the json representation of the object", required = true) String dto);

    @Operation(summary = "Create a resource by giving it's name and Id",
            tags = { "Generic" },
            description ="specify the entity name, the record id, and as body, the list of the fields to create",
            responses = {
                    @ApiResponse(responseCode="200", description = "resource successfully updated but not content exposed except the hypermedia"),
                    @ApiResponse(responseCode = "400", description = "bad request when input not well formed")
    })
    @POST
    @Path("/{entityName}")
    Response create(@Parameter(description = "the entity name", required = true) @PathParam("entityName") String entityName,
                    @Parameter(description = "dto the json representation of the object", required = true) String dto);

    @Operation(summary = "Delete a resource by giving it's name and Id",
            tags = { "Generic" },
            description ="specify the entity name, the record id, and as body, the list of the fields to delete",
            responses = {
                    @ApiResponse(responseCode="200", description = "resource successfully updated but not content exposed except the hypermedia"),
                    @ApiResponse(responseCode = "404", description = "baseEntityObject not found", content = @Content(schema = @Schema(implementation = ApiException.class))),
                    @ApiResponse(responseCode = "400", description = "bad request when input not well formed")
    })
    @DELETE
    @Path("/{entityName}/{id}")
    Response delete(@Parameter(description = "the entity name", required = true) @PathParam("entityName") String entityName,
                    @Parameter(description = "The id here is the database primary key of the record to delete", required = true) @PathParam("id") Long id);

    @Operation(summary = "Get versions information about OpenCell components",
            tags = { "Generic" },
            description ="Return a list of OpenCell's components version information",
            responses = {
                    @ApiResponse(responseCode="200", description = "resource successfully updated but not content exposed except the hypermedia")
            })
    @GET
    @Path("/version")
    Response getVersions();

    @GET
//    @Path("/{entityName: ^(?!all$).*$}/{id}")
    @Path("/{entityName}/{id}")
    @Operation(summary = "Generic single endpoint to retrieve resources by ID",
            tags = { "Generic" },
            description ="You need to specify the entity name of **plural form** and the record id" +
                    "Also, the first character of the entity name is **lowercase**. If the entity name is a " +
                    "single word, its characters should be written in lowercase, for example *sellers*. " +
                    "If the entity name is a compound word, only the first character of its first single word is " +
                    "lowercase, the first character of remaining words should be written in uppercase, " +
                    "for example *paymentMethods*.\n\n"
                    + "You can specify the configuration of the research in your path. For example, you can define : \n\n " +
                    "- an offset : **/sellers?offset=0** \n\n" +
                    "- a limit : **/sellers?limit=5** \n\n" +
                    "- a combination of offset and limit : **/sellers?offset=0&limit=5** \n\n" +
                    "Or you can define : \n\n " +
                    "- a sort by the field *description* : **/sellers?sort=description** \n\n" +
                    "- a sort type DESCENDING : **/sellers?sort=-description** \n\n" +
                    "- a combination of sort field and sort type : **/sellers?sort=-description,tradingCurrency** \n\n" +
                    "Or you can filter a field by using an interval of values : \n\n" +
                    "- a left-bounded interval : **/sellers?id=2,** \n\n" +
                    "- a right-bounded interval : **/sellers?id=,5** \n\n" +
                    "- a bounded interval : **/sellers?id=2,5** \n\n",
            responses = {
                    @ApiResponse(responseCode="200", description = "paginated results successfully retrieved with hypermedia links"),
                    @ApiResponse(responseCode = "404", description = "baseEntityObject not found", content = @Content(schema = @Schema(implementation = ApiException.class))),
                    @ApiResponse(responseCode = "400", description = "bad request when entityName not well formed or entity unrecognized")
            })
    Response getEntity(@Parameter(description = "the entity name", required = true) @PathParam("entityName") String entityName,
                       @Parameter(description = "The id here is the database primary key of the wanted record", required = true) @PathParam("id") Long id,
                       @Parameter(description = "requestDto carries the wanted fields ex: {fields = [code, description]}", required = true) GenericPagingAndFiltering searchConfig);

    @GET
    @Path("/{entityName}")
    @Operation(summary = "Generic single endpoint to retrieve paginated records of an entity",
            tags = { "Generic" },
            description = "You need to specify the entity name which is of the **plural form**. " +
                    "Also, the first character of the entity name is **lowercase**. If the entity name is a " +
                    "single word, its characters should be written in lowercase, for example *sellers*. " +
                    "If the entity name is a compound word, only the first character of its first single word is " +
                    "lowercase, the first character of remaining words should be written in uppercase, " +
                    "for example *paymentMethods*.\n\n"
                    + "You can specify the configuration of the research in your path. For example, you can define : \n\n " +
                    "- an offset : **/sellers?offset=0** \n\n" +
                    "- a limit : **/sellers?limit=5** \n\n" +
                    "- a combination of offset and limit : **/sellers?offset=0&limit=5** \n\n" +
                    "Or you can define : \n\n " +
                    "- a sort by the field *description* : **/sellers?sort=description** \n\n" +
                    "- a sort type DESCENDING : **/sellers?sort=-description** \n\n" +
                    "- a combination of sort field and sort type : **/sellers?sort=-description,tradingCurrency** \n\n" +
                    "Or you can filter a field by using an interval of values : \n\n" +
                    "- a left-bounded interval : **/sellers?id=2,** \n\n" +
                    "- a right-bounded interval : **/sellers?id=,5** \n\n" +
                    "- a bounded interval : **/sellers?id=2,5** \n\n",
            responses = {
                    @ApiResponse(responseCode="200", description = "paginated results successfully retrieved with hypermedia links"),
                    @ApiResponse(responseCode = "404", description = "baseEntityObject not found", content = @Content(schema = @Schema(implementation = ApiException.class))),
                    @ApiResponse(responseCode = "400", description = "bad request when entityName not well formed or entity unrecognized")
            })
    Response getAllEntities(@Parameter(description = "The entity name", required = true) @PathParam("entityName") String entityName,
                            @Parameter(description = "Additional properties such as limit, offset, sort, interval values, etc.",
                                    schema = @Schema(implementation = UriInfo.class)) @Context UriInfo uriInfo,
//                            @Parameter(description = "Additional properties such as limit, offset, sort, interval values, etc.",
//                                    schema = @Schema(implementation = Object.class)) @QueryParam("Additional properties") String additionalProperties,
//                            @Parameter(description = "Query param offset") @QueryParam("offset") String offset,
//                            @Parameter(description = "Query param sort") @QueryParam("sort") String sort,
//                            @Parameter(description = "Query param interval for data fields") @QueryParam("interval") String interval,
                            @Parameter(description = "The header request such as Accept-Language, If-Modified-Since, etc.") @Context HttpHeaders requestHeaders )
            throws JsonProcessingException, ParseException;

    @GET
    @Path("/entities")
    @Operation(summary = "This endpoint is used to retrieve the full list of queryable entities",
            tags = { "Generic" },
            description = "This endpoint retrieves all possible queryable entities in the database.",
            responses = {
                    @ApiResponse(responseCode="200", description = "paginated results successfully retrieved with hypermedia links"),
                    @ApiResponse(responseCode = "404", description = "the full list of entities not found",
                            content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response getFullListEntities();

    @GET
    @Path("/entities/{entityName}")
    @Operation(summary = "This endpoint is used to retrieve the fields and corresponding types of an entity",
            tags = { "Generic" },
            description ="You need to specify an entity name. \n\n" +
                    "Given the entity name, this endpoint returns the list of its fields and corresponding types. " +
                    "The entity name should not be written in the plural form. For example, *customer*.",
            responses = {
                    @ApiResponse(responseCode="200", description = "paginated results successfully retrieved with hypermedia links"),
                    @ApiResponse(responseCode = "404", description = "the full list of entities not found",
                            content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response getRelatedFieldsOfEntity( @Parameter(description = "The entity name", required = true) @PathParam("entityName") String entityName );

    @HEAD
    @Path("/{entityName}/{id}")
    @Operation(summary = "Generic single endpoint to check existence of a resource by ID",
            tags = { "Generic" },
            description ="You need to specify the entity name and the record id. " +
                    "This endpoint returns only a status, without body.",
            responses = {
                    @ApiResponse(responseCode="200", description = "the resources exists in the database"),
                    @ApiResponse(responseCode = "404", description = "baseEntityObject not found", content = @Content(schema = @Schema(implementation = ApiException.class))),
                    @ApiResponse(responseCode = "400", description = "bad request when entityName not well formed or entity unrecognized")
            })
    Response head(@Parameter(description = "The entity name", required = true) @PathParam("entityName") String entityName,
                       @Parameter(description = "The id of the record that you want to check existence", required = true) @PathParam("id") Long id);

}
