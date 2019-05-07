package com.hubwiz.demo;

import java.util.List;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import com.google.protobuf.ByteString;

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
        case "inc": return inc(stub,args);
        case "reset": return reset(stub,args);
        case "value": return value(stub,args);
        case "addTwin": return addTwin(stub, args);
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
        
        return newSuccessResponse(String.format("twin add success, ID: %s\n",args.get(0)));
    }
    
    //查询Twin
    private Response getTwinsByUserID(ChaincodeStub stub,List<String> args){
        String userID = args.get(0);
        
        CompositeKey key = stub.createCompositeKey("userID->twinID",userID);
        
        QueryResultsIterator<KeyValue> iterator = stub.getStateByPartialCompositeKey(key.toString());
        for(KeyValue kv: iterator){
            CompositeKey fkey = stub.splitCompositeKey(kv.getKey());
            String twinID = fkey.getAttributes().get(1);
            String Info = stub.getStringState(twinID);
            System.out.format("car %s: %s\n", twinID,Info);
        }
        return newSuccessResponse();
    }
    
  private Response inc(ChaincodeStub stub, List<String> args){
    int step = 1;
    if(args.size() > 0 ) step = Integer.parseInt(args.get(0));
    String valueStr = stub.getStringState("value");
    int value = Integer.parseInt(valueStr);
    value += step;
    stub.putStringState("value",Integer.toString(value));
    return newSuccessResponse(String.format("updated => %d",value));
  }
  
  private Response reset(ChaincodeStub stub, List<String> args){
    stub.putStringState("value","0");
    return newSuccessResponse("reset to zero");
  }
  
  private Response value(ChaincodeStub stub, List<String> args){
    String value = stub.getStringState("value");
    return newSuccessResponse(value,ByteString.copyFromUtf8(value).toByteArray());
  }
  
  public static void main(String[] args){
    System.out.format("hello\n");
    new CounterChaincode().start(args);
  }
}
