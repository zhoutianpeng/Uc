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

public class InvertChaincode extends ChaincodeBase {
  @Override
  public Response init(ChaincodeStub stub){
    System.out.println("InvertChaincode init");
    return newSuccessResponse();
  }
  
  @Override
  public Response invoke(ChaincodeStub stub){
    String fcn = stub.getFunction();
    System.out.format("InvertChaincode invoke: %s \n",fcn);
    List<String> args = stub.getParameters();
    switch(fcn){
      case "insertInvertIndex": return insertInvertIndex(stub,args);
      case "getIndexByKyeword": return getIndexByKyeword(stub,args);
      case "getIndexs": return getIndexByKyeword(stub,args);
    }
    return newErrorResponse("unimplemented method");
  }
  
  private Response insertInvertIndex(ChaincodeStub stub,List<String> args){
    System.out.println("insertInvertIndex invoke");

    if(args.size() != 3) return newErrorResponse("Arguments mismatch");
        
    JSONObject jo = new JSONObject();
    jo.put("invertID",args.get(0));
    jo.put("keyWord",args.get(1));
    jo.put("keyIndex",args.get(2));
    
    stub.putStringState(args.get(0),jo.toString());
    
    return newSuccessResponse("success",toBytes("success"));
  }
  
  private Response getIndexByKyeword(ChaincodeStub stub,List<String> args){
    System.out.println("getIndexByKyeword invoke");

    if(args.size() != 1) return newErrorResponse("Arguments mismatch");
    String keyWord = args.get(0);

    // "{
    //   \"selector\": {
    //       \"keyWord\": \"%s\"
    //   }
    // }"
    String qs = String.format("{ \"selector\": { \"keyWord\": \"%s\" }}" ,keyWord);
    QueryResultsIterator<KeyValue> iterator = stub.getQueryResult(qs);
  
    JSONArray ja = new JSONArray();
    for(KeyValue kv: iterator){
      JSONObject jo = new JSONObject(kv.getStringValue());
      ja.put(jo);	
    }

    if(ja.length() == 0){
      return newSuccessResponse("none",toBytes("none"));
    }
    else{
      String ret = ja.toString();
      return newSuccessResponse(ret,toBytes(ret));
    }
  }
  
  private Response getIndexs(ChaincodeStub stub,List<String> args){
  System.out.println("getIndexs invoke");

  JSONArray ja = new JSONArray();
    for(int i = 0 ; i < args.size() ;i++){
        String qs = String.format("{ \"selector\": { \"keyWord\": \"%s\" }}" , args.get(i));
         QueryResultsIterator<KeyValue> iterator = stub.getQueryResult(qs);
          for(KeyValue kv: iterator){
            JSONObject jo = new JSONObject(kv.getStringValue());
            ja.put(jo);	
          }  
    } 
    return newSuccessResponse(ja.toString(),toBytes(ja.toString()));
  }

  private byte[] toBytes(String str){
    return ByteString.copyFromUtf8(str).toByteArray();
  }
  
  public static void main(String[] args){
    System.out.println("InvertChaincode==>");
    new InvertChaincode().start(args);
  }
}