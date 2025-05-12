package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

import java.util.HashMap;
import java.util.Map;

public class FuncaoQuatroHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final DynamoDbClient dynamoDb = DynamoDbClient.create();
    private static final String TABLE_NAME = "ListaMercado";

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        try {
            String itemId = (String) input.get("itemId");
            String pk = (String) input.get("pk");

            if (itemId == null || pk == null) {
                return createErrorResponse(400, "Campos obrigatórios 'itemId' e 'pk' são necessários.");
            }

            String pkFormatado = "list#" + pk;
            String sk = "item#" + itemId;

            Map<String, AttributeValue> key = new HashMap<>();
            key.put("PK", AttributeValue.builder().s(pkFormatado).build());
            key.put("SK", AttributeValue.builder().s(sk).build());

            // Verifica se o item existe antes de tentar excluir
            GetItemRequest getRequest = GetItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .key(key)
                    .build();

            Map<String, AttributeValue> existingItem = dynamoDb.getItem(getRequest).item();

            if (existingItem == null || existingItem.isEmpty()) {
                // Item já foi removido ou nunca existiu
                return Map.of("success", true, "message", "Item já não existe ou foi removido anteriormente.");
            }

            // Realiza a exclusão
            DeleteItemRequest deleteRequest = DeleteItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .key(key)
                    .build();

            dynamoDb.deleteItem(deleteRequest);

            return Map.of("success", true, "message", "Item excluído com sucesso.");

        } catch (Exception e) {
            context.getLogger().log("Erro ao excluir item: " + e.getMessage());
            return createErrorResponse(500, "Erro interno ao tentar excluir o item.");
        }
    }

    private Map<String, Object> createErrorResponse(int statusCode, String message) {
        return Map.of(
                "success", false,
                "statusCode", statusCode,
                "message", message
        );
    }
}
