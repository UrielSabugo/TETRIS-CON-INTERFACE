package com.example;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Tetris extends JPanel {
    private static final int ANCHO_TABLERO = 10;
    private static final int ALTO_TABLERO = 20;
    private static final int TAMANO_BLOQUE = 30;

    private int[][] tablero;
    private Forma formaActual;
    private Random random;

    public Tetris() {
        tablero = new int[ALTO_TABLERO][ANCHO_TABLERO];
        random = new Random();
        formaActual = nuevaForma();

        // Configurar el temporizador para la caída de las piezas
        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moverAbajo();
            }
        });
        timer.start();

        // Configurar los controles del teclado
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        moverIzquierda();
                        break;
                    case KeyEvent.VK_RIGHT:
                        moverDerecha();
                        break;
                    case KeyEvent.VK_DOWN:
                        moverAbajo();
                        break;
                    case KeyEvent.VK_UP:
                        rotar();
                        break;
                }
                repaint();
            }
        });
    }

    private Forma nuevaForma() {
        // Crear una nueva forma aleatoria
        int tipo = random.nextInt(7);
        return new Forma(tipo);
    }

    private void moverIzquierda() {
        if (puedeMoverse(formaActual, -1, 0)) {
            formaActual.x--;
        }
    }

    private void moverDerecha() {
        if (puedeMoverse(formaActual, 1, 0)) {
            formaActual.x++;
        }
    }

    private void moverAbajo() {
        if (puedeMoverse(formaActual, 0, 1)) {
            formaActual.y++;
        } else {
            fijarForma();
        }
        repaint();
    }

    private void rotar() {
        Forma formaRotada = formaActual.rotar();
        if (puedeMoverse(formaRotada, 0, 0)) {
            formaActual = formaRotada;
        }
    }

    private boolean puedeMoverse(Forma forma, int dx, int dy) {
        for (int i = 0; i < 4; i++) {
            int nuevoX = forma.x + forma.getX(i) + dx;
            int nuevoY = forma.y + forma.getY(i) + dy;
            if (nuevoX < 0 || nuevoX >= ANCHO_TABLERO || nuevoY >= ALTO_TABLERO) {
                return false;
            }
            if (nuevoY >= 0 && tablero[nuevoY][nuevoX] != 0) {
                return false;
            }
        }
        return true;
    }

    private void fijarForma() {
        for (int i = 0; i < 4; i++) {
            int x = formaActual.x + formaActual.getX(i);
            int y = formaActual.y + formaActual.getY(i);
            if (y >= 0) {
                tablero[y][x] = formaActual.tipo + 1;
            }
        }
        eliminarLineasCompletas();
        formaActual = nuevaForma();
    }

    private void eliminarLineasCompletas() {
        for (int y = ALTO_TABLERO - 1; y >= 0; y--) {
            boolean lineaCompleta = true;
            for (int x = 0; x < ANCHO_TABLERO; x++) {
                if (tablero[y][x] == 0) {
                    lineaCompleta = false;
                    break;
                }
            }
            if (lineaCompleta) {
                for (int yy = y; yy > 0; yy--) {
                    System.arraycopy(tablero[yy-1], 0, tablero[yy], 0, ANCHO_TABLERO);
                }
                for (int x = 0; x < ANCHO_TABLERO; x++) {
                    tablero[0][x] = 0;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Dibujar el tablero
        for (int y = 0; y < ALTO_TABLERO; y++) {
            for (int x = 0; x < ANCHO_TABLERO; x++) {
                if (tablero[y][x] != 0) {
                    dibujarBloque(g, x, y, tablero[y][x] - 1);
                }
            }
        }
        
        // Dibujar la forma actual
        for (int i = 0; i < 4; i++) {
            int x = formaActual.x + formaActual.getX(i);
            int y = formaActual.y + formaActual.getY(i);
            dibujarBloque(g, x, y, formaActual.tipo);
        }
    }

    private void dibujarBloque(Graphics g, int x, int y, int tipo) {
        Color[] colores = {Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.ORANGE,
                           Color.BLUE, Color.GREEN, Color.RED};
        g.setColor(colores[tipo]);
        g.fillRect(x * TAMANO_BLOQUE, y * TAMANO_BLOQUE, TAMANO_BLOQUE, TAMANO_BLOQUE);
        g.setColor(Color.BLACK);
        g.drawRect(x * TAMANO_BLOQUE, y * TAMANO_BLOQUE, TAMANO_BLOQUE, TAMANO_BLOQUE);
    }

    private class Forma {
        private int x;
        private int y;
        private int tipo;
        private int[][][] formas = {
            {{0, 0}, {0, 1}, {1, 0}, {1, 1}},  // Cuadrado
            {{0, -1}, {0, 0}, {0, 1}, {0, 2}}, // I
            {{-1, 0}, {0, 0}, {1, 0}, {0, 1}}, // T
            {{0, 0}, {1, 0}, {0, 1}, {1, 1}},  // Cuadrado
            {{-1, -1}, {0, -1}, {0, 0}, {0, 1}}, // L
            {{1, -1}, {0, -1}, {0, 0}, {0, 1}},  // J
            {{-1, 0}, {0, 0}, {1, 0}, {1, 1}}   // S
        };

        public Forma(int tipo) {
            this.tipo = tipo;
            x = ANCHO_TABLERO / 2;
            y = 0;
        }

        public int getX(int index) {
            return formas[tipo][index][0];
        }

        public int getY(int index) {
            return formas[tipo][index][1];
        }

        public Forma rotar() {
            Forma nuevaForma = new Forma(tipo);
            nuevaForma.x = x;
            nuevaForma.y = y;
            for (int i = 0; i < 4; i++) {
                int tempX = getX(i);
                nuevaForma.formas[tipo][i][0] = -getY(i);
                nuevaForma.formas[tipo][i][1] = tempX;
            }
            return nuevaForma;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(ANCHO_TABLERO * TAMANO_BLOQUE, ALTO_TABLERO * TAMANO_BLOQUE);
        frame.setResizable(false);
        frame.add(new Tetris());
        frame.setVisible(true);
    }
}