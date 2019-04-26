#!/bin/bash
ARTIFACT=artifact

configtxlator proto_encode --input ${ARTIFACT}/config.json --type common.Config --output ${ARTIFACT}/config.pb

yaml2json ${ARTIFACT}/modified.yaml > ${ARTIFACT}/modified.json
configtxlator proto_encode --input ${ARTIFACT}/modified.json --type common.Config --output ${ARTIFACT}/modified.pb

configtxlator compute_update --original ${ARTIFACT}/config.pb --updated ${ARTIFACT}/modified.pb --channel_id ch1 --output ${ARTIFACT}/update.pb
configtxlator proto_decode  --input ${ARTIFACT}/update.pb --type common.ConfigUpdate | jq . > ${ARTIFACT}/update.json

echo '{"payload":{"header":{"channel_header":{"channel_id":"ch1", "type":2}},"data":{"config_update":'$(cat ${ARTIFACT}/update.json)'}}}' | jq . > ${ARTIFACT}/update-envelope.json
configtxlator proto_encode --input ${ARTIFACT}/update-envelope.json --type common.Envelope --output ${ARTIFACT}/update-envelope.pb

FABRIC_CFG_PATH=./network \
CORE_PEER_MSPCONFIGPATH=crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp \
CORE_PEER_LOCALMSPID=Org1MSP \
peer channel signconfigtx -f ${ARTIFACT}/update-envelope.pb

FABRIC_CFG_PATH=./network \
CORE_PEER_MSPCONFIGPATH=crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp \
CORE_PEER_LOCALMSPID=Org1MSP \
peer channel update -f ${ARTIFACT}/update-envelope.pb -c ch1 -o localhost:7050

echo done.