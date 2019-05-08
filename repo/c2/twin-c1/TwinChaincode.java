package com.hubwiz.demo;

import java.util.List;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import com.google.protobuf.ByteString;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.List;


public class TwinChaincode extends ChaincodeBase{
    @Override
    public Response init(ChaincodeStub stub){
        System.out.println("--------init--------");
        return newSuccessResponse();
    }
    @Override
    public Response invoke(ChaincodeStub stub){
        System.out.println("--------invoke--------");
        String fcn = stub.getFunction();
        List<String> args = stub.getParameters();
        System.out.format("fcn => %s",fcn);
        switch(fcn){
            case "addTwin": return addTwin(stub, args);
            case "getTwinByTwinID": return getTwinByTwinID(stub, args);
            case "getTwinsByUserID": return getTwinsByUserID(stub, args);
        }
        return newErrorResponse("unimplemented method => " + fcn);
    }
    
    //添加Twin
    private Response addTwin(ChaincodeStub stub, List<String> args) {
        
        if (args.size() != 3) {
            return newErrorResponse("Incorrect number of arguments. Expecting 3");
        }
        
        String twinID = args.get(0);
        String userID = args.get(1);
        String info = args.get(2);
        
        //使用twinID->userID构建复合键
        CompositeKey key = stub.createCompositeKey("userID->twinID",twinID,userID);
        
        stub.putStringState(key.toString(),info);
        System.out.println("--------addTwin--------");
        System.out.println("key:"+key.toString() + "info:"+info);
        
        return newSuccessResponse(String.format("twin add success, ID: %s\n",args.get(0)));
    }
    
    //get Twin by TwinID
    private Response getTwinByTwinID(ChaincodeStub stub,List<String> args){
        String twinID = args.get(0);
        
        CompositeKey key = stub.createCompositeKey("userID->twinID",twinID);
        
        QueryResultsIterator<KeyValue> iterator = stub.getStateByPartialCompositeKey(key.toString());
        String Info = "";
        for(KeyValue kv: iterator){
            CompositeKey fkey = stub.splitCompositeKey(kv.getKey());
            String tempID = fkey.getAttributes().get(1);
            Info = stub.getStringState(tempID);
            System.out.println("--------getTwinByTwinID--------");
            System.out.format("Twin %s: %s\n", tempID,Info);
            
        }
        return newSuccessResponse(Info,toBytes(Info));
    }
    
    //get Twins by UserID
    private Response getTwinsByUserID(ChaincodeStub stub,List<String> args){
        String userID = args.get(0);
        
        CompositeKey key = stub.createCompositeKey("userID->twinID", userID);
        QueryResultsIterator<KeyValue> iterator = stub.getStateByPartialCompositeKey(key.toString());
        
        JSONArray resultSet = new JSONArray();
        for(KeyValue kv: iterator){
            CompositeKey fkey = stub.splitCompositeKey(kv.getKey());
            String twinID = fkey.getAttributes().get(1);
            String Info = stub.getStringState(twinID);
            
            System.out.println("--------getTwinsByUserID--------");
            System.out.format("Twin %s: %s\n", twinID,Info);
            
            JSONObject jo = new JSONObject(Info);
            resultSet.put(jo);
        }
         String ret = resultSet.toString();
        return newSuccessResponse(ret,toBytes(ret));
    }
    
    
    private byte[] toBytes(String str){
        return ByteString.copyFromUtf8(str).toByteArray();
    }
    
    public static void main(String[] args){
        System.out.format("hello\n");
        new TwinChaincode().start(args);
    }
}
