package com.ezticket.ecpay.service;

import com.ezticket.web.product.pojo.Pdetails;
import com.ezticket.web.product.pojo.Porder;
import com.ezticket.web.product.pojo.Product;
import com.ezticket.web.product.repository.PdetailsRepository;
import com.ezticket.web.product.repository.PorderRepository;
import com.ezticket.web.product.repository.ProductDAO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FonPayService {
    @Autowired
    private PorderRepository porderRepository;
    @Autowired
    private PdetailsRepository pdetailsRepository;
    @Autowired
    private ProductDAO productRepository;

    static String FONPAY_API_KEY = "279152534347";
    static String FONPAY_API_SECRET = "40uku10lpcfMSFjmbKD5";
    static String FONPAY_API_MERCHANT_CODE = "ME18315275";
    static String PAYMENT_CREATE_ORDER = "PaymentCreateOrder";
    public String fonpayCheckout(Integer porderno) throws IOException {
        URL url = new URL("https://test-api.fonpay.tw/api/payment/" + PAYMENT_CREATE_ORDER);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("key", FONPAY_API_KEY);
        con.setRequestProperty("secret", FONPAY_API_SECRET);
        con.setRequestProperty("merchantCode", FONPAY_API_MERCHANT_CODE);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("User-Agent", "Tibame_Student");
        con.setRequestProperty("X-ignore", "true");
        con.setDoOutput(true);
        // 建立當下時間
        LocalDateTime now = LocalDateTime.now();
        // 延長10分鐘付款時間並轉換格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String Now = now.plusMinutes(10).format(formatter);

        // 取得自身IP
        InetAddress ip = null;
        try {
            // 使用可能會拋出異常的方法
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            // 處理異常
            System.err.println(e);
        }
        String hostname = ip.getHostAddress();
        // 付款完成後，接收GET請求更新資料庫
        String local = "http://" + hostname + ":8085" + "/ecpay/paymentReturn";
        // 若有提供，系統會在訂單狀態異動時由背景呼叫,此網址通知電商，
        String httpsUrl = "https://6bf1-2001-b011-a406-9e1a-59a9-bd18-7e2d-d643.jp.ngrok.io";
        String callback = httpsUrl + "/ecpay/fonpay/return";
        // 商品明細
        String itemList = "";
        List<Pdetails> pdetailsList = pdetailsRepository.findByPorderno(porderno);
        for (int i = 0; i < pdetailsList.size(); i++) {
            Pdetails pdetails = pdetailsList.get(i);
            Product product = productRepository.getByPrimaryKey(pdetails.getPdetailsNo().getProductno());
            itemList += "{'itemName':'" + product.getPname() + "','itemQuantity':'" + pdetails.getPorderqty() + "'},";
        }
        Porder porder = porderRepository.getReferenceById(porderno);
        String jsonInputString = "{ " +
                "'request':{" +
                "'note':'Test'," +
                "'paymentNo':'ezTicket" + porderno + "'," +
                "'totalPrice':" + porder.getPchecktotal().toString() + "," +
                "'paymentDueDate':'" + Now + "'," +
                "'itemName':'ezTicket 售票平台_周邊商品'," +
                "'callbackUrl':'" + callback + "'," +
                "'redirectUrl':'" + local + "'," +
                "'includeItemList':[" + itemList + "]" +
                "}," +
                "'basic':{" +
                "'appVersion':'0.9'," +
                "'os':'IOS'," +
                "'appName':'POSTMAN'," +
                "'latitude':24.25," +
                "'clientIp':'61.216.102.83'," +
                "'lang':'zh_TW'," +
                "'deviceId':'123456789'," +
                "'longitude':124.25" +
                "}}";

        String paymentTransactionId = "";
        String paymentUrl ="";
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
            String jsonResponse = response.toString();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            paymentTransactionId = jsonNode.get("result").get("payment").get("paymentTransactionId").asText();
            paymentUrl = jsonNode.get("result").get("payment").get("paymentUrl").asText();
        }
        porder.setPaymenttransactionid(paymentTransactionId);
        porderRepository.save(porder);
        return paymentUrl;
    }
}

