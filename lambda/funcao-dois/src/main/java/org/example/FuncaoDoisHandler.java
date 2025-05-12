package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FuncaoDoisHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final DynamoDbClient dynamoDb = DynamoDbClient.create();
    private static final String TABLE_NAME = "ListaMercado";

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        String nome = (String) input.get("name");
        String data = (String) input.get("date");

        if (nome == null || data == null) {
            throw new IllegalArgumentException("Campos obrigatórios 'name' e 'date' são necessários.");
        }

        String pk = "list#" + data.replace("-", "");
        String itemId = UUID.randomUUID().toString();
        String sk = "item#" + itemId;
        String createdAt = Instant.now().toString();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("PK", AttributeValue.builder().s(pk).build());
        item.put("SK", AttributeValue.builder().s(sk).build());
        item.put("name", AttributeValue.builder().s(nome).build());
        item.put("status", AttributeValue.builder().s("todo").build());
        item.put("createdAt", AttributeValue.builder().s(createdAt).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build();

        dynamoDb.putItem(request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("item", Map.of(
                "PK", pk,
                "SK", sk,
                "name", nome,
                "status", "todo",
                "createdAt", createdAt
        ));

        return response;
    }
}
