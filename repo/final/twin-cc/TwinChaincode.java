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

public class TwinChaincode extends ChaincodeBase {
  @Override
  public Response init(ChaincodeStub stub){
    System.out.println("TwinChaincode init");
    return newSuccessResponse();
  }
  
  @Override
  public Response invoke(ChaincodeStub stub){
    String fcn = stub.getFunction();
    System.out.format("UserChaincode invoke: %s \n",fcn);
    List<String> args = stub.getParameters();
    switch(fcn){
      case "getTwinsByuserID": return getTwinsByuserID(stub,args);
      case "getTwinByTwinID": return getTwinByTwinID(stub,args);
      case "insertTwin": return insertTwin(stub,args);
      case "getTwinsByTwinIDArray": return getTwinsByTwinIDArray(stub,args);
       case "delByTwinID": return delByTwinID(stub,args);
    }
    return newErrorResponse("unimplemented method");
  }
  
  private Response insertTwin(ChaincodeStub stub,List<String> args){
    System.out.println("insertTwin invoke");

    if(args.size() != 5) return newErrorResponse("Arguments mismatch");
        
    JSONObject jo = new JSONObject();
    jo.put("twinID",args.get(0));
    jo.put("userID",args.get(1));
    jo.put("twinContent",args.get(2));
    jo.put("twinImg",args.get(3));
    jo.put("time",args.get(4));
    
    stub.putStringState(args.get(0),jo.toString());
    
    return newSuccessResponse("success",toBytes("success"));
  }

  private Response getTwinsByuserID(ChaincodeStub stub,List<String> args){
    System.out.println("getTwinsByuserID invoke");

    if(args.size() != 1) return newErrorResponse("Arguments mismatch");
    String userID = args.get(0);

    String qs = String.format("{ \"selector\": { \"userID\": \"%s\" }}" ,userID);
    QueryResultsIterator<KeyValue> iterator = stub.getQueryResult(qs);

    JSONArray ja = new JSONArray();
    for(KeyValue kv: iterator){
      JSONObject jo = new JSONObject(kv.getStringValue());
      ja.put(jo);	
    }
    
     return newSuccessResponse(ja.toString(),toBytes(ja.toString()));

  }

  
  private Response getTwinByTwinID(ChaincodeStub stub,List<String> args){
     System.out.println("getTwinByTwinID invoke");
    if(args.size() != 1) return newErrorResponse("Arguments mismatch");
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
  
  private Response getTwinsByTwinIDArray(ChaincodeStub stub,List<String> args){
    System.out.println("getTwinsByTwinIDArray invoke");

    JSONArray ja = new JSONArray();
    for(int i = 0 ; i < args.size() ;i++){
        String qs = String.format("{ \"selector\": { \"twinID\": \"%s\" }}" ,twinID);
         QueryResultsIterator<KeyValue> iterator = stub.getQueryResult(qs);
          for(KeyValue kv: iterator){
            JSONObject jo = new JSONObject(kv.getStringValue());
            ja.put(jo);	
          }  
    } 
    return newSuccessResponse(ja.toString(),toBytes(ra.toString()));
  }
  
  private Response delByTwinID(ChaincodeStub stub,List<String> args){
    System.out.println("delByTwinID invoke");
    stub.delState(args.get(0));
    return newSuccessResponse("success",toBytes("success"));
  }

  private byte[] toBytes(String str){
    return ByteString.copyFromUtf8(str).toByteArray();
  }
  
  public static void main(String[] args){
    System.out.println("TwinChaincode==>");
    new TwinChaincode().start(args);
  }
}