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

public class FavourChaincode extends ChaincodeBase {
  @Override
  public Response init(ChaincodeStub stub){
    System.out.println("FavourChaincode init");
    return newSuccessResponse();
  }
  
  @Override
  public Response invoke(ChaincodeStub stub){
    String fcn = stub.getFunction();
    System.out.format("FavourChaincode invoke: %s \n",fcn);
    List<String> args = stub.getParameters();
    switch(fcn){
      case "insertFavour": return insertFavour(stub,args);
      case "getFavourByID": return getFavourByID(stub,args);
      case "delFavourByID": return delFavourByID(stub,args);
    }
    return newErrorResponse("unimplemented method");
  }
  
  private Response insertFavour(ChaincodeStub stub,List<String> args){
    System.out.println("insertFavour invoke");
    if(args.size() != 5) return newErrorResponse("Arguments mismatch");
        
    JSONObject jo = new JSONObject();
    jo.put("favourID",args.get(0));
    jo.put("twinID",args.get(1));
    jo.put("userID",args.get(2));

    stub.putStringState(args.get(0),jo.toString());
    return newSuccessResponse("success");
  }

  private Response getFavourByID(ChaincodeStub stub,List<String> args){
    System.out.println("getFavourByID invoke");

    String twinID = args.get(0);

    String qs = String.format("{ \"selector\": { \"twinID\": \"%s\" }}" ,twinID);
    QueryResultsIterator<KeyValue> iterator = stub.getQueryResult(qs);

    JSONArray ja = new JSONArray();
    for(KeyValue kv: iterator){
      JSONObject jo = new JSONObject(kv.getStringValue());
      ja.put(jo);	
    }
    
     return newSuccessResponse(ja.toString,toBytes(ja.toStringt));
  }

   private Response delFavourByID(ChaincodeStub stub,List<String> args){
    System.out.println("delFavourByID invoke");

    String twinID = args.get(0);
    String userID = args.get(1);

    String qs = String.format("{ \"selector\": { \"twinID\": \"%s\" , \"userID\" : \"%s\"}}" ,twinID ,userID);
    QueryResultsIterator<KeyValue> iterator = stub.getQueryResult(qs);
    JSONObject jo;
    for(KeyValue kv: iterator){
      JSONObject jo = new JSONObject(kv.getStringValue());  
    }

    String id = jo.getString(favourID);
    stub.delState(args.get(0));
    return newSuccessResponse("success");
  }


  private byte[] toBytes(String str){
    return ByteString.copyFromUtf8(str).toByteArray();
  }
  
  public static void main(String[] args){
    System.out.println("FavourChaincode==>");
    new FavourChaincode().start(args);
  }
}