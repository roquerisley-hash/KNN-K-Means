import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import javax.imageio.ImageIO;

public class GraficoCotovelo {
    public static void salvar(double[] inercias, Path arquivo) throws IOException {
        int largura = 960;
        int altura = 540;
        int esquerda = 90;
        int topo = 75;
        int base = 465;
        int direita = 900;
        double maximo = 0;
        for (double inercia : inercias) {
            maximo = Math.max(maximo, inercia);
        }
        maximo = Math.max(maximo * 1.15, 1e-9);
        BufferedImage imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagem.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, largura, altura);
            g.setFont(new Font("SansSerif", Font.BOLD, 22));
            g.setColor(Color.DARK_GRAY);
            g.drawString("Método do cotovelo — K-Means com min-max", esquerda, 34);
            g.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g.drawString("Menor inércia entre cinco sementes para cada K", esquerda, 56);
            for (int marca = 0; marca <= 5; marca++) {
                int y = base - (base - topo) * marca / 5;
                g.setColor(new Color(225, 229, 235));
                g.drawLine(esquerda, y, direita, y);
                g.setColor(Color.DARK_GRAY);
                g.drawString(String.format(Locale.ROOT, "%.2f", maximo * marca / 5), 28, y + 5);
            }
            g.drawLine(esquerda, topo, esquerda, base);
            g.drawLine(esquerda, base, direita, base);
            g.drawString("K (número de grupos)", 410, 515);
            g.rotate(-Math.PI / 2);
            g.drawString("Inércia", -290, 17);
            g.rotate(Math.PI / 2);
            g.setStroke(new BasicStroke(2));
            int anteriorX = 0;
            int anteriorY = 0;
            for (int i = 0; i < inercias.length; i++) {
                int x = esquerda + (direita - esquerda) * i / (inercias.length - 1);
                int y = base - (int) Math.round((base - topo) * inercias[i] / maximo);
                g.setColor(new Color(36, 88, 160));
                if (i > 0) {
                    g.drawLine(anteriorX, anteriorY, x, y);
                }
                g.fillOval(x - 5, y - 5, 10, 10);
                g.setColor(Color.DARK_GRAY);
                g.drawString(Integer.toString(i + 1), x - 4, base + 24);
                String valor = String.format(Locale.ROOT, "%.3f", inercias[i]);
                g.drawString(valor, x - g.getFontMetrics().stringWidth(valor) / 2, y - 13);
                anteriorX = x;
                anteriorY = y;
            }
        } finally {
            g.dispose();
        }
        if (!ImageIO.write(imagem, "png", arquivo.toFile())) {
            throw new IOException("Não foi possível salvar o gráfico PNG.");
        }
    }
}
