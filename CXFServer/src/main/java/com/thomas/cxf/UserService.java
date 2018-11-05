package com.thomas.cxf;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@WebService
@Path(value = "/users/")
public interface UserService {

    @GET  //只有GET请求才会到这来
    @Path("/")  //http://ip:port/users
    @Produces(MediaType.APPLICATION_JSON) //指定返回数据的格式
    //可以设置多个，@Produces({MediaType.APPLICATION_JSON，MediaType.APPLICATION_XML})
    //根据请求头  Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8设置
    //设置application/xml则返回xml格式，设置json则返回json
    List<User> getUsers();
    @DELETE
    @Path("{id}") //http://ip:port/users/1
    @Produces(MediaType.APPLICATION_JSON) //指定返回数据的格式
    Response delete(@PathParam("id") int id);
    @GET
    @Path("{id}") //http://ip:port/users/1
    @Produces(MediaType.APPLICATION_JSON) //指定返回数据的格式
    User getUser(@PathParam("id") int id);
    @POST
    @Path("add")
    Response insert(User user);
    @PUT
    @Path("update")
    Response update(User user);

}
