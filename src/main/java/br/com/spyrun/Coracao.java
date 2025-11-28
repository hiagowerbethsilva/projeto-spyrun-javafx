package br.com.spyrun;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

/**
 * Representa um coração coletável no mapa.
 */
public class Coracao extends MapObject {
    private Image imagemCoracao;

    public Coracao(double x, double y, Image imagemCoracao) {
        super(x, y, imagemCoracao.getWidth(), imagemCoracao.getHeight(), null);
        this.imagemCoracao = imagemCoracao;
    }

    /**
     * Desenha o coração na tela, ajustado pela posição da câmera.
     * @param gc O contexto gráfico do canvas.
     * @param cameraX A posição X da câmera.
     * @param cameraY A posição Y da câmera.
     */
    @Override
    public void draw(GraphicsContext gc, double cameraX, double cameraY) {
        gc.drawImage(imagemCoracao, x - cameraX, y - cameraY);
    }

    /**
     * Retorna o retângulo de colisão do coração.
     * @return um Rectangle representando os limites do objeto.
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}