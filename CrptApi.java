package documentCreator;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
//В ТЗ документе шаблон данных документа в JSON содержал число 109. Я не смог понять его назначение, нахождение числа делает JSON не читаемым,
//но я решил его оставить в конвертере документа в JSON
//Передаваемая подпись в документе задумывалась для криптографии? Оставил ее как одно из полей документа
//В любом случае очень надеюсь на обратную связь и разъяснения
public class CrptApi {
    private Thread threadSender;
    private final TimeUnit timeUnit;
    private final Queue<LocalDateTime> timePoints;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final int requestLimit;
    private Queue<Doc> queueRequest = new ArrayDeque<>();
    private final String urlApiCreate = "https://ismp.crpt.ru/api/v3/lk/documents/create";

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        timePoints = new ConcurrentLinkedDeque<>();
    }

    public void createDoc(Doc doc, String signature) throws URISyntaxException, IOException, InterruptedException {
        queueRequest.add(createDocFromJSONWithSignature(doc, signature));
        startThreadSender();
    }
    public Doc newDoc(){
        return new Doc();
    }
    private void sender() throws URISyntaxException, IOException, InterruptedException {
        while (permissionToSend() && queueRequest.iterator().hasNext()) {
            sendDoc(queueRequest.poll());
        }
    }
    private void startThreadSender(){
        if(timePoints.size() == requestLimit) {
            if(threadSender == null || !threadSender.isAlive()) {
                threadSender = new Thread(new Sender());
                threadSender.start();
            }
        }else{
            try {
                sender();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private HttpResponse<String> sendDoc(Doc doc) throws URISyntaxException, IOException, InterruptedException {
        JsonSerialization jsonSerialization = new JsonSerialization();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI(urlApiCreate))
                .POST(HttpRequest.BodyPublishers.ofString(jsonSerialization.toJson(doc)))
                .build();
        HttpResponse<String> response;
        timePoints.add(LocalDateTime.now());
        System.out.println("sendDoc");
        return response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    private Doc createDocFromJSONWithSignature(Doc doc, String signature) {
        return doc;
    }
    public class Doc {
        private Description description;
        private String doc_id;
        private String doc_status;
        private String doc_type;
        private String someNumber;
        private boolean importRequest;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private Date production_date;
        private String production_type;
        private Product[] products;
        private Date reg_date;
        private String reg_number;
        private String signature;

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public Description getDescription() {
            return description;
        }
        public Description newDescription(){
            return new Description();
        }
        public void setDescription(Description description) {
            this.description = description;
        }

        public String getDoc_id() {
            return doc_id;
        }

        public void setDoc_id(String doc_id) {
            this.doc_id = doc_id;
        }

        public String getDoc_status() {
            return doc_status;
        }

        public void setDoc_status(String doc_status) {
            this.doc_status = doc_status;
        }

        public String getDoc_type() {
            return doc_type;
        }

        public void setDoc_type(String doc_type) {
            this.doc_type = doc_type;
        }

        public String getSomeNumber() {
            return someNumber;
        }

        public void setSomeNumber(String someNumber) {
            this.someNumber = someNumber;
        }

        public boolean isImportRequest() {
            return importRequest;
        }

        public void setImportRequest(boolean importRequest) {
            this.importRequest = importRequest;
        }

        public String getOwner_inn() {
            return owner_inn;
        }

        public void setOwner_inn(String owner_inn) {
            this.owner_inn = owner_inn;
        }

        public String getParticipant_inn() {
            return participant_inn;
        }

        public void setParticipant_inn(String participant_inn) {
            this.participant_inn = participant_inn;
        }

        public String getProducer_inn() {
            return producer_inn;
        }

        public void setProducer_inn(String producer_inn) {
            this.producer_inn = producer_inn;
        }

        public Date getProduction_date() {
            return production_date;
        }

        public void setProduction_date(Date production_date) {
            this.production_date = production_date;
        }

        public String getProduction_type() {
            return production_type;
        }

        public void setProduction_type(String production_type) {
            this.production_type = production_type;
        }
        public Product newProduct(){
            return new Product();
        }

        public Product[] getProducts() {
            return products;
        }

        public void setProducts(Product[] products) {
            this.products = products;
        }

        public Date getReg_date() {
            return reg_date;
        }

        public void setReg_date(Date reg_date) {
            this.reg_date = reg_date;
        }

        public String getReg_number() {
            return reg_number;
        }

        public void setReg_number(String reg_number) {
            this.reg_number = reg_number;
        }

        public class Description{
            private String participantInn;

            public String getParticipantInn() {
                return participantInn;
            }

            public void setParticipantInn(String participantInn) {
                this.participantInn = participantInn;
            }
        }
        public class Product{
            private String certificate_document;
            private Date certificate_document_date;
            private String certificate_document_number;
            private String owner_inn;
            private String producer_inn;
            private Date production_date;
            private String tnved_code;
            private String uit_code;
            private String uitu_code;

            public String getCertificate_document() {
                return certificate_document;
            }

            public void setCertificate_document(String certificate_document) {
                this.certificate_document = certificate_document;
            }

            public Date getCertificate_document_date() {
                return certificate_document_date;
            }

            public void setCertificate_document_date(Date certificate_document_date) {
                this.certificate_document_date = certificate_document_date;
            }

            public String getCertificate_document_number() {
                return certificate_document_number;
            }

            public void setCertificate_document_number(String certificate_document_number) {
                this.certificate_document_number = certificate_document_number;
            }

            public String getOwner_inn() {
                return owner_inn;
            }

            public void setOwner_inn(String owner_inn) {
                this.owner_inn = owner_inn;
            }

            public String getProducer_inn() {
                return producer_inn;
            }

            public void setProducer_inn(String producer_inn) {
                this.producer_inn = producer_inn;
            }

            public Date getProduction_date() {
                return production_date;
            }

            public void setProduction_date(Date production_date) {
                this.production_date = production_date;
            }

            public String getTnved_code() {
                return tnved_code;
            }

            public void setTnved_code(String tnved_code) {
                this.tnved_code = tnved_code;
            }

            public String getUit_code() {
                return uit_code;
            }

            public void setUit_code(String uit_code) {
                this.uit_code = uit_code;
            }

            public String getUitu_code() {
                return uitu_code;
            }

            public void setUitu_code(String uitu_code) {
                this.uitu_code = uitu_code;
            }
        }
    }
    private boolean permissionToSend() {
        timerLimit();
        if (timePoints.size() < requestLimit) {
            return true;
        }
        return false;
    }
    private void timerLimit() {
        LocalDateTime localDateTime = timePoints.peek();
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        if(localDateTime != null) {
            if (timeUnit.name().equals(TimeUnit.DAYS.name())) {
                if (ChronoUnit.DAYS.between(localDateTime, localDateTimeNow) > 1) {
                    timePoints.poll();
                }
            } else if (timeUnit.name().equals(TimeUnit.MINUTES.name())) {
                if (ChronoUnit.MINUTES.between(localDateTime, localDateTimeNow) > 1) {
                    timePoints.poll();
                }
            } else if (timeUnit.name().equals(TimeUnit.HOURS.name())) {
                if (ChronoUnit.HOURS.between(localDateTime, localDateTimeNow) > 1) {
                    timePoints.poll();
                }
            } else if (timeUnit.name().equals(TimeUnit.MICROSECONDS.name())) {
                if (ChronoUnit.MICROS.between(localDateTime, localDateTimeNow) > 1) {
                    timePoints.poll();
                }
            } else if (timeUnit.name().equals(TimeUnit.MILLISECONDS.name())) {
                if (ChronoUnit.MILLIS.between(localDateTime, localDateTimeNow) > 1) {
                    timePoints.poll();
                }
            } else if (timeUnit.name().equals(TimeUnit.NANOSECONDS.name())) {
                if (ChronoUnit.NANOS.between(localDateTime, localDateTimeNow) > 1) {
                    timePoints.poll();
                }
            } else if (timeUnit.name().equals(TimeUnit.SECONDS.name())) {
                if (ChronoUnit.SECONDS.between(localDateTime, localDateTimeNow) > 1) {
                    timePoints.poll();
                }
            }
        }
    }
    private class JsonSerialization{
        public String toJson(Doc doc){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{");
            stringBuilder.append(toJsonObject("description", new String[]{doc.getDescription().getParticipantInn()}));
            stringBuilder.append(",");
            stringBuilder.append(toJsonField("signature", doc.getSignature()));
            stringBuilder.append(",");
            stringBuilder.append(toJsonField("doc_id", doc.getDoc_id()));
            stringBuilder.append(",");
            stringBuilder.append(toJsonField("doc_status", doc.getDoc_status()));
            stringBuilder.append(",");
            stringBuilder.append(toJsonField("doc_type", doc.getDoc_type()));
            stringBuilder.append(",");
            stringBuilder.append(doc.getSomeNumber());
            stringBuilder.append(toJsonField("importRequest", String.valueOf(doc.isImportRequest())));
            stringBuilder.append(",");
            stringBuilder.append(toJsonField("owner_inn", doc.getOwner_inn()));
            stringBuilder.append(",");
            stringBuilder.append(toJsonField("participant_inn", doc.getParticipant_inn()));
            stringBuilder.append(",");
            stringBuilder.append(toJsonField("producer_inn", doc.getProducer_inn()));
            stringBuilder.append(",");
            stringBuilder.append(toJsonField("production_date", doc.getProduction_date().toString()));
            stringBuilder.append(",");
            stringBuilder.append(toJsonField("production_type", doc.getProduction_type()));
            stringBuilder.append(",");
            stringBuilder.append(toJsonArray("products", doc.getProducts()));
            stringBuilder.append(",");
            stringBuilder.append(toJsonField("reg_date", doc.getReg_date().toString()));
            stringBuilder.append(toJsonField("reg_number", doc.getReg_number()));
            System.out.println(stringBuilder.toString());
            return stringBuilder.toString();
        }
        private String toJsonField(String name, String arg){
            String json = wrap(name) + ":" + wrap(arg);
            return json;
        }
        private String toJsonArray(String name, Doc.Product arg[]){
            String json = wrap(name)
                    + ":"
                    + "["
                    + "{";
            for(int i = 0; i < arg.length; i++){
                Doc.Product product = arg[i];
                String jsonProduct = toJsonField("certificate_document", product.getCertificate_document())
                        + ","
                        + toJsonField("certificate_document_date", product.getCertificate_document_date().toString())
                        + ","
                        + toJsonField("certificate_document_number", product.getCertificate_document_number())
                        + ","
                        + toJsonField("owner_inn", product.getOwner_inn())
                        + ","
                        + toJsonField("producer_inn", product.getProducer_inn())
                        + ","
                        + toJsonField("production_date", product.getProduction_date().toString())
                        + ","
                        + toJsonField("tnved_code", product.getTnved_code())
                        + ","
                        + toJsonField("uit_code", product.getUit_code())
                        + ","
                        + toJsonField("uitu_code", product.getUitu_code())
                        + "}";
                if(i + 1 != arg.length){
                    jsonProduct += ",";
                }
                json += jsonProduct;
            }
            json += "]";
            return json;
        }
        private String toJsonObject(String name, String arg[]){
            String json = wrap(name) + ":" + "{";
            for(int i = 0; i < arg.length; i++){
                json += (wrap(arg[i]));
            }
            json += "}";
            return json;
        }
        private String wrap(String arg){
            String wrapped = "\"" + arg + "\"";
            return wrapped;
        }
    }
    private class Sender implements Runnable{

        @Override
        public void run() {
                while (queueRequest.size() > 0) {
                    try {
                        sender();
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("Thread close");
            }
        }
    }
