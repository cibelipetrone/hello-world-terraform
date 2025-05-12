package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

public class FuncaoTresHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final DynamoDbClient dynamoDb = DynamoDbClient.create();
    private static final String TABLE_NAME = "ListaMercado";

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        try {
            String itemId = (String) input.get("itemId");
            String dataAtual = (String) input.get("dataAtual");
            String novoNome = (String) input.get("novoNome");
            String novaData = (String) input.get("novaData");
            String novoStatus = (String) input.get("novoStatus");

            if (itemId == null || dataAtual == null) {
                return createErrorResponse(400, "Campos obrigatórios 'itemId' e 'dataAtual' são necessários.");
            }

            String pkAtual = "list#" + dataAtual;
            String sk = "item#" + itemId;

            GetItemRequest getRequest = GetItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .key(Map.of(
                            "PK", AttributeValue.builder().s(pkAtual).build(),
                            "SK", AttributeValue.builder().s(sk).build()
                    ))
                    .build();

            Map<String, AttributeValue> itemAtual = dynamoDb.getItem(getRequest).item();

            if (itemAtual == null || itemAtual.isEmpty()) {
                return createErrorResponse(404, "Item não encontrado para a chave fornecida.");
            }

            String pkNova = novaData != null ? "list#" + novaData : pkAtual;

            // Atualização simples (sem nova data)
            if (pkAtual.equals(pkNova)) {
                Map<String, AttributeValueUpdate> updates = new HashMap<>();

                if (novoNome != null) {
                    updates.put("name", AttributeValueUpdate.builder()
                            .value(AttributeValue.builder().s(novoNome).build())
                            .action(AttributeAction.PUT)
                            .build());
                }

                if (novoStatus != null) {
                    updates.put("status", AttributeValueUpdate.builder()
                            .value(AttributeValue.builder().s(novoStatus).build())
                            .action(AttributeAction.PUT)
                            .build());
                }

                if (!updates.isEmpty()) {
                    UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                            .tableName(TABLE_NAME)
                            .key(Map.of(
                                    "PK", AttributeValue.builder().s(pkAtual).build(),
                                    "SK", AttributeValue.builder().s(sk).build()
                            ))
                            .attributeUpdates(updates)
                            .returnValues(ReturnValue.ALL_NEW)
                            .build();

                    Map<String, AttributeValue> itemAtualizado = dynamoDb.updateItem(updateRequest).attributes();
                    return Map.of("success", true, "item", convertItemToMap(itemAtualizado));
                } else {
                    return Map.of("success", true, "item", convertItemToMap(itemAtual));
                }

            } else {
                // Se mudança de data
                Map<String, AttributeValue> novoItem = new HashMap<>(itemAtual);
                novoItem.put("PK", AttributeValue.builder().s(pkNova).build());

                if (novoNome != null) {
                    novoItem.put("name", AttributeValue.builder().s(novoNome).build());
                }
                if (novoStatus != null) {
                    novoItem.put("status", AttributeValue.builder().s(novoStatus).build());
                }

                // Inserir novo item
                dynamoDb.putItem(PutItemRequest.builder()
                        .tableName(TABLE_NAME)
                        .item(novoItem)
                        .build());

                // Deletar item antigo
                dynamoDb.deleteItem(DeleteItemRequest.builder()
                        .tableName(TABLE_NAME)
                        .key(Map.of(
                                "PK", AttributeValue.builder().s(pkAtual).build(),
                                "SK", AttributeValue.builder().s(sk).build()
                        ))
                        .build());

                return Map.of("success", true, "item", convertItemToMap(novoItem));
            }

        } catch (Exception e) {
            context.getLogger().log("Erro ao atualizar item: " + e.getMessage());
            return createErrorResponse(500, "Erro interno ao processar a solicitação.");
        }
    }

    private Map<String, Object> convertItemToMap(Map<String, AttributeValue> item) {
        Map<String, Object> result = new HashMap<>();
        item.forEach((k, v) -> result.put(k, v.s()));
        return result;
    }

    private Map<String, Object> createErrorResponse(int statusCode, String message) {
        return Map.of(
                "success", false,
                "statusCode", statusCode,
                "message", message
        );
    }
}
