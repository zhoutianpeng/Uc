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

public class UserChaincode extends ChaincodeBase {
  @Override
  public Response init(ChaincodeStub stub){
    System.out.println("UserChaincode init");
    return newSuccessResponse();
  }
  
  @Override
  public Response invoke(ChaincodeStub stub){
    String fcn = stub.getFunction();
    System.out.format("UserChaincode invoke: %s \n",fcn);
    List<String> args = stub.getParameters();
    switch(fcn){
      case "registerUser": return registerUser(stub,args);
      case "loginUser": return loginUser(stub,args);
      case "updateuserInfo": return updateuserInfo(stub,args);
    }
    return newErrorResponse("unimplemented method");
  }
  
  private Response registerUser(ChaincodeStub stub,List<String> args){
    System.out.println("registerUser invoke");

    if(args.size() != 4) return newErrorResponse("Arguments mismatch");
        
    JSONObject jo = new JSONObject();
    jo.put("userID",args.get(0));
    jo.put("userName",args.get(1));
    jo.put("password",args.get(2));
    jo.put("userImg",args.get(3));
    
    stub.putStringState(args.get(0),jo.toString());
    
    return newSuccessResponse("success");
  }
  
  private Response loginUser(ChaincodeStub stub,List<String> args){
    System.out.println("loginUser invoke");

    if(args.size() != 2) return newErrorResponse("Arguments mismatch");
    String userName = args.get(0);
    String password = args.get(1);

    String qs = String.format(
    "{
      \"selector\": {
          \"userName\": \"%s\",
          \"password\": \"%s\"
      }
    }"
    ,userName , password);
    QueryResultsIterator<KeyValue> iterator = stub.getQueryResult(qs);
    if(iterator.size() == 0){
      newErrorResponse("none");
    }

    else{
      JSONArray ja = new JSONArray();
      for(KeyValue kv: iterator){
        JSONObject jo = new JSONObject(kv.getStringValue());
        ja.put(jo);	
      }
      String ret = ja.toString();
      return newSuccessResponse(ret,toBytes(ret));
    }
  }
  
  private Response updateuserInfo (ChaincodeStub stub,List<String> args){
    System.out.println("updateuserInfo invoke");
    
    if(args.size() != 4) return newErrorResponse("Arguments mismatch");
        
    JSONObject jo = new JSONObject();
    jo.put("userID",args.get(0));
    jo.put("userName",args.get(1));
    jo.put("password",args.get(2));
    jo.put("userImg",args.get(3));
    
    stub.putStringState(args.get(0),jo.toString());

     return newSuccessResponse("success");
  }
  
  private byte[] toBytes(String str){
    return ByteString.copyFromUtf8(str).toByteArray();
  }
  
  public static void main(String[] args){
    System.out.println("UserChaincode==>");
    new WizChaincode().start(args);
  }
}