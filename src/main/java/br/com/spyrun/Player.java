package br.com.spyrun;

import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

/**
 * Classe Player.
 * Representa o protagonista controlado pelo jogador.
 * Armazena posição, velocidade, vida e os sprites de personagem.
 */
public class Player implements Movable {

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    // Posição do jogador no mundo (coordenadas lógicas, não da tela)
    public double x;
    public double y;

    // Velocidade de movimento do jogador (pixels por segundo)
    public double speed = 200;

    // Vida atual do jogador (em corações)
    public int health = 5;
    public static final int VIDA_MAXIMA = 5;

    // Direção atual do jogador para selecionar o sprite
    public Direction direction = Direction.DOWN;

    // Sprites do jogador
    private Image spriteUp;
    private Image spriteDown;
    private Image spriteLeft;
    private Image spriteRight;
    private Image defaultSprite;

    /**
     * Construtor do player.
     * @param x posição inicial X no mundo
     * @param y posição inicial Y no mundo
     */
    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        loadSprites();
    }

    /**
     * Carrega as imagens (sprites) do jogador a partir da pasta de resources.
     */
    private void loadSprites() {
        try {
            // Carrega o sprite padrão primeiro como garantia
            defaultSprite = new Image(getClass().getResourceAsStream("/sprites/Player-down.png"));
        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO: Não foi possível carregar o sprite padrão Player-down.png");
            defaultSprite = null;
        }
        try {
            spriteUp = new Image(getClass().getResourceAsStream("/sprites/Player-up.png"));
        } catch (Exception e) {
            System.err.println("Não foi possível carregar o sprite Player-up.png");
            spriteUp = null;
        }
        try {
            spriteDown = new Image(getClass().getResourceAsStream("/sprites/Player-down.png"));
        } catch (Exception e) {
            System.err.println("Não foi possível carregar o sprite Player-down.png");
            spriteDown = null;
        }
        try {
            spriteLeft = new Image(getClass().getResourceAsStream("/sprites/Player-left.png"));
        } catch (Exception e) {
            System.err.println("Não foi possível carregar o sprite Player-left.png");
            spriteLeft = null;
        }
        try {
            spriteRight = new Image(getClass().getResourceAsStream("/sprites/Player-right.png"));
        } catch (Exception e) {
            System.err.println("Não foi possível carregar o sprite Player-right.png");
            spriteRight = null;
        }
    }

    /**
     * Retorna a imagem correspondente à direção atual do jogador.
     * @return A imagem do sprite a ser desenhada.
     */
    public Image getImage() {
        switch (direction) {
            case UP:
                return spriteUp != null ? spriteUp : defaultSprite;
            case DOWN:
                return spriteDown != null ? spriteDown : defaultSprite;
            case LEFT:
                return spriteLeft != null ? spriteLeft : defaultSprite;
            case RIGHT:
                return spriteRight != null ? spriteRight : defaultSprite;
            default:
                return defaultSprite;
        }
    }

    /**
     * Aplica dano ao jogador, reduzindo um coração.
     * @param amount A quantidade de dano (não utilizada, sempre reduz 1).
     * @return true se a vida chegou a 0 ou menos.
     */
    public boolean takeDamage(double amount) {
        if (amount > 0) {
            health--;
        }
        return health <= 0;
    }

    /**
     * Cura o jogador, restaurando um coração.
     */
    public void curar() {
        if (health < VIDA_MAXIMA) {
            health++;
        }
    }

    /**
     * Retorna o retângulo de colisão do jogador.
     * @return um Rectangle 30x30 centrado no jogador.
     */
    public Rectangle getBounds() {
        return new Rectangle(x - 15, y - 15, 30, 30);
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }
}
