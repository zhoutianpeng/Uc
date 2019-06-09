package com.hubwiz.demo;

import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.hyperledger.fabric.shim.ledger.KeyValue;

import com.google.protobuf.ByteString;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.List;

public class CommentChaincode extends ChaincodeBase {
  @Override
  public Response init(ChaincodeStub stub){
    System.out.println("CommentChaincode init");
    return newSuccessResponse();
  }
  
  @Override
  public Response invoke(ChaincodeStub stub){
    String fcn = stub.getFunction();
    System.out.format("CommentChaincode invoke: %s \n",fcn);
    List<String> args = stub.getParameters();
    switch(fcn){
      case "insertComment": return insertComment(stub,args);
      case "getCommentByID": return getCommentByID(stub,args);
    }
    return newErrorResponse("unimplemented method");
  }
  
  private Response insertComment(ChaincodeStub stub,List<String> args){
    System.out.println("insertComment invoke");
    if(args.size() != 5) return newErrorResponse("Arguments mismatch");
        
    JSONObject jo = new JSONObject();
    jo.put("commentID",args.get(0));
    jo.put("twinID",args.get(1));
    jo.put("userID",args.get(2));
    jo.put("content",args.get(3));
    jo.put("time",args.get(4));

    stub.putStringState(args.get(0),jo.toString());
    return newSuccessResponse("success",toBytes("success"));
  }

   private Response getCommentByID(ChaincodeStub stub,List<String> args){
    System.out.println("getCommentByID invoke");

    String twinID = args.get(0);

    String qs = String.format("{ \"selector\": { \"twinID\": \"%s\" }}" ,twinID);
    QueryResultsIterator<KeyValue> iterator = stub.getQueryResult(qs);

    JSONArray ja = new JSONArray();
    for(KeyValue kv: iterator){
      JSONObject jo = new JSONObject(kv.getStringValue());
      ja.put(jo);	
    }
    
     return newSuccessResponse(ja.toString(),toBytes(ja.toString()));
  }


  private byte[] toBytes(String str){
    return ByteString.copyFromUtf8(str).toByteArray();
  }
  
  public static void main(String[] args){
    System.out.println("CommentChaincode==>");
    new CommentChaincode().start(args);
  }
}