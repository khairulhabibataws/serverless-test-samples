package com.example.sdk;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import com.example.CustomerValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class EdaClient extends BaseJsonSchemaValidator{

    private String product;
    private String action;
    private String targetSchemaUrl;

    private CustomerValidator validator;

    public EdaClient(String product, String action, String targetSchemaUrl){
        this.product = product;
        this.action = action;
        this.targetSchemaUrl = targetSchemaUrl;
    }

    public void send(JsonNode payload) throws URISyntaxException{
        
        S3Client s3 = S3Client.builder().region(Region.AP_SOUTHEAST_1).credentialsProvider(ProfileCredentialsProvider.create("dev0")).build();
        // get schema from url
        JsonSchema schema = getJsonSchemaFromUrl(targetSchemaUrl);
        System.out.println("schema >>>> " + schema.toString());

        // initialize validator
        schema.initializeValidators();

        // validate payload
        Set<ValidationMessage> diffs = schema.validate(payload);
        if(diffs.size() > 0){
            // iterate diffs on iterator
            for(ValidationMessage diff : diffs){
                System.out.println(diff.getMessage());
            }
            // throw exception
            throw new RuntimeException("Payload does not match schema");
        }else{
            // create simpledateformat for ISO format
            DateTimeFormatter timeformat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            LocalDateTime currentDT = LocalDateTime.now();
            // send payload to S3 bucket using aws sdk
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(product + "-" + action + "-mkhabib")
                    .key(action + currentDT.format(timeformat) + ".json")
                    .build();
            validator.isValidAddress(payload);
            RequestBody requestBody = RequestBody.fromString(payload.asText());


            // send with s3 client
            s3.putObject(objectRequest, requestBody);
            s3.close();
        }
    }
    
}
