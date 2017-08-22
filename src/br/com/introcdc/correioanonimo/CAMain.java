package br.com.introcdc.correioanonimo;

import twitter4j.DirectMessage;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class CAMain {

    public static final List<String> banneds = Arrays.asList("761981979940294656", "3245351787");
    private static final Calendar calendar = Calendar.getInstance();
    public static final File global_log = new File("global_log.txt");
    public static final HashMap<String, String> logs = new HashMap<>();
    public static final Random random = new Random();
    private static final Timer timer = new Timer();
    public static PrintWriter writer;

    public static void addToLog(final String key, final String message) throws Exception {
        if (CAMain.writer == null) {
            CAMain.reloadCache();
        }
        CAMain.logs.put(key, message);
        CAMain.writer.println(message);
        CAMain.writer.flush();
    }

    public static String convertToDate(final long number) {
        CAMain.calendar.setTimeInMillis(number);
        return CAMain.calendar.get(Calendar.DAY_OF_MONTH) + "/" + (CAMain.calendar.get(Calendar.MONTH) + 1) + "/" + CAMain.calendar.get(Calendar.YEAR) + " - " + CAMain.calendar.get(Calendar.HOUR_OF_DAY) + ":" + CAMain.calendar.get(Calendar.MINUTE) + ":" + CAMain.calendar.get(Calendar.SECOND);
    }

    public static void main(final String[] args) throws Exception {
        System.out.println("Carregando TwitterBot...");
        System.out.println("Carregando IA do CorreioAnonimo...");
        System.out.println("Autenticando bots e logando na conta...");
        for (final CA_IA ia : CA_IA.values()) {
            ia.login();
        }
        System.out.println("Logado com sucesso!");
        CAMain.reloadCache();
        CAMain.readyForUpdate(1);
    }

    public static void postProcess(int number) {
        System.out.print("*");
        if (number == 6 || number == 12 || number == 18 || number == 24) {
            System.out.print("-");
        }
        final int Number = ++number;
        CAMain.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (Number <= 30) {
                    CAMain.postProcess(Number);
                }
            }
        }, 10 * 1000);
    }

    public static void readyForUpdate(final int seconds) {
        CAMain.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    CAMain.updateDirectMessages(null);
                } catch (final Exception e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        }, seconds * 1000);
    }

    public static void reloadCache() throws Exception {
        System.out.println("Recarregando cache...");
        if (!CAMain.global_log.exists()) {
            CAMain.global_log.createNewFile();
        }
        CAMain.logs.clear();
        final Scanner scanner = new Scanner(CAMain.global_log);
        while (scanner.hasNextLine()) {
            final String message = scanner.nextLine();
            CAMain.logs.put(message.split("//")[0], message);
        }
        CAMain.writer = new PrintWriter(CAMain.global_log);
        for (final String key : CAMain.logs.values()) {
            CAMain.writer.println(key);
        }
        CAMain.writer.flush();
        scanner.close();
        System.out.println("Cache recarregado com sucesso!");
    }

    public static void updateDirectMessages(final CA_IA ia) throws Exception {
        final CA_IA selectedIA = ia != null ? ia : CA_IA.values()[CAMain.random.nextInt(CA_IA.values().length)];
        System.out.println("");
        System.out.println("Procurando atualizações de Direct Message a partir do Bot " + selectedIA.toString() + "...");
        if (!selectedIA.isEnabled()) {
            System.out.println("O Sistema escolheu um bot desativado, escolhendo outro...");
            CAMain.readyForUpdate(1);
            return;
        }
        int messages = 0;
        int cache = 0;
        for (final DirectMessage message : selectedIA.getTwitter().getDirectMessages()) {
            cache++;
            final String identifer = message.getSenderId() + "-" + message.getId() + "-" + message.getRecipientId() + "-" + message.getCreatedAt().getTime();
            if (banneds.contains(String.valueOf(message.getSenderId()))) {
                continue;
            }
            final String key = identifer + "//<@" + message.getSender().getScreenName() + "> - " + message.getText().replace("\n", " - ") + " - (" + CAMain.convertToDate(message.getCreatedAt().getTime()) + ")";
            if (!CAMain.logs.containsKey(identifer)) {
                StringBuilder receiver = new StringBuilder();
                StringBuilder result = new StringBuilder();
                boolean already = false;
                boolean send = false;
                for (final String arg : message.getText().split(" ")) {
                    if (arg.startsWith("@") && arg.length() > 3 && !already) {
                        receiver.append(" ").append(arg);
                        already = true;
                        send = true;
                    } else {
                        result.append(" ").append(arg);
                    }
                }
                if (send) {
                    messages++;
                    System.out.println("DM: " + key);
                    try {
                        final String resultMessage = "Para:" + receiver + " - Msg:" + result;
                        if (resultMessage.length() <= 140) {
                            selectedIA.getTwitter().updateStatus(resultMessage);
                        } else {
                            selectedIA.getTwitter().updateStatus(resultMessage.substring(0, 140));
                        }
                        CAMain.addToLog(identifer, key);
                        selectedIA.getTwitter().sendDirectMessage(message.getSenderId(), "Mensagem anonima enviada para" + receiver + " pelo bot " + selectedIA.toString() + " com sucesso!");
                        System.out.println("Mensagem enviada com sucesso!");
                    } catch (final Exception e) {
                        System.out.println("Ocorreu um erro ao enviar a mensagem! Veja o log!");
                        final String errorMessage = e.getMessage();
                        System.out.println("Mensagem: " + errorMessage);
                        if (errorMessage.contains("- 187")) {
                            CAMain.addToLog(identifer, key);
                            selectedIA.getTwitter().sendDirectMessage(message.getSenderId(), "Este mensagem já foi enviada para" + receiver + "!");
                        } else {
                            selectedIA.setEnabled(false);
                            selectedIA.getTwitter().sendDirectMessage(message.getSenderId(), "Ocorreu um erro ao enviar a mensagem para" + receiver + " pelo bot " + selectedIA.toString() + "! Bot sendo desativado...");
                        }
                    }
                }
            }
        }
        System.out.println("Atualização completa! Total de mensagens enviadas: " + messages + " - Cachê: " + cache);
        System.out.println("Próxima atualização:");
        System.out.println("||||||-||||||-||||||-||||||-||||||");
        CAMain.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CAMain.postProcess(1);
            }
        }, 9900);
        CAMain.readyForUpdate(5 * 60);
    }

}
