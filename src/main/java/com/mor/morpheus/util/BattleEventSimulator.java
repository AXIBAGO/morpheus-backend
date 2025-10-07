package com.mor.morpheus.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class BattleEventSimulator {

    private static final String API_URL = "http://localhost:8080/events/batch";
    private static final List<String> PLAYERS = List.of("anka", "mira", "hans");
    private static final Random RANDOM = new Random();

    private static final Map<String, Integer> playerSeq = new HashMap<>();

    public static void main(String[] args) throws Exception {
        System.out.println("🔥 开始模拟战斗事件发送...");
        while (true) {
            // 构造一个批次的随机事件
            List<Map<String, Object>> events = new ArrayList<>();

            int count = RANDOM.nextInt(3) + 1; // 每轮 1~3 条事件
            for (int i = 0; i < count; i++) {
                String player = PLAYERS.get(RANDOM.nextInt(PLAYERS.size()));
                int seq = playerSeq.merge(player, 1, Integer::sum);
                long ts = System.currentTimeMillis();
                int damage = 100 + RANDOM.nextInt(201); // 100~300

                Map<String, Object> e = new LinkedHashMap<>();
                e.put("playerId", player);
                e.put("seq", seq);
                e.put("ts", ts);
                e.put("damage", damage);
                events.add(e);
            }

            // 发送 HTTP POST
            sendEvents(events);

            Thread.sleep(1000); // 每秒发送一批
        }
    }

    private static void sendEvents(List<Map<String, Object>> events) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = toJson(events);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            int code = conn.getResponseCode();
            System.out.println("→ 发送 " + events.size() + " 条事件，HTTP状态：" + code);

        } catch (Exception e) {
            System.err.println("发送失败：" + e.getMessage());
        }
    }

    private static String toJson(List<Map<String, Object>> events) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < events.size(); i++) {
            Map<String, Object> e = events.get(i);
            sb.append("{");
            int j = 0;
            for (var entry : e.entrySet()) {
                if (j++ > 0) sb.append(",");
                sb.append("\"").append(entry.getKey()).append("\":");
                if (entry.getValue() instanceof Number)
                    sb.append(entry.getValue());
                else
                    sb.append("\"").append(entry.getValue()).append("\"");
            }
            sb.append("}");
            if (i < events.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
