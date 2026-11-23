package com.pesanyuk.gui;

import com.pesanyuk.exception.InputTidakValidException;
import com.pesanyuk.exception.MenuNotFoundException;
import com.pesanyuk.manager.MenuManager;
import com.pesanyuk.model.Makanan;
import com.pesanyuk.model.MenuItem;
import com.pesanyuk.model.Minuman;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Dialog Form Modal untuk Tambah dan Edit Menu (Makanan / Minuman).
 */
public class MenuFormDialog extends JDialog {

    public interface FormCallback {
        void onSaved();
    }

    private MenuItem itemToEdit;
    private MenuManager menuManager;
    private FormCallback callback;

    private JTextField txtId;
    private JTextField txtNama;
    private JTextField txtHarga;
    private JRadioButton rbMakanan;
    private JRadioButton rbMinuman;

    private JPanel atributCard;
    private JLabel lblAtribut;
    private JComboBox<String> cbAtribut;

    public MenuFormDialog(Window parent, MenuItem itemToEdit, MenuManager menuManager, FormCallback callback) {
        super(parent, itemToEdit == null ? "Tambah Menu Baru" : "Ubah Data Menu", ModalityType.APPLICATION_MODAL);
        this.itemToEdit = itemToEdit;
        this.menuManager = menuManager;
        this.callback = callback;

        setSize(420, 520);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout(0, 16));

        initComponents();
        if (itemToEdit != null) {
            populateData();
        }
    }

    private void initComponents() {
        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel title = new JLabel(itemToEdit == null ? "Tambah Menu Baru" : "Ubah Menu: " + itemToEdit.getNama());
        title.setFont(UITheme.FONT_SECTION);
        title.setForeground(UITheme.TEXT_DARK);

        JLabel sub = new JLabel("Lengkapi data makanan atau minuman restoran");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);

        header.add(title);
        header.add(Box.createRigidArea(new Dimension(0, 4)));
        header.add(sub);
        add(header, BorderLayout.NORTH);

        // Form Fields Container
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(16, 24, 16, 24));

        // 1. Kategori (Radio Makanan vs Minuman)
        JLabel lblKat = new JLabel("Kategori Menu:");
        lblKat.setFont(UITheme.FONT_BOLD);
        lblKat.setForeground(UITheme.TEXT_DARK);

        rbMakanan = new JRadioButton("Makanan", true);
        rbMinuman = new JRadioButton("Minuman");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbMakanan);
        bg.add(rbMinuman);

        rbMakanan.setFont(UITheme.FONT_REGULAR);
        rbMinuman.setFont(UITheme.FONT_REGULAR);
        rbMakanan.setBackground(Color.WHITE);
        rbMinuman.setBackground(Color.WHITE);

        rbMakanan.addActionListener(e -> updateAtributOptions(true));
        rbMinuman.addActionListener(e -> updateAtributOptions(false));

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        radioPanel.setOpaque(false);
        radioPanel.add(rbMakanan);
        radioPanel.add(rbMinuman);

        // 2. ID Menu
        JLabel lblId = new JLabel("ID Menu (misal: M001 / D001):");
        lblId.setFont(UITheme.FONT_BOLD);
        txtId = createStyledTextField();

        // 3. Nama Menu
        JLabel lblNama = new JLabel("Nama Menu:");
        lblNama.setFont(UITheme.FONT_BOLD);
        txtNama = createStyledTextField();

        // 4. Harga
        JLabel lblHarga = new JLabel("Harga Satuan (Rp):");
        lblHarga.setFont(UITheme.FONT_BOLD);
        txtHarga = createStyledTextField();

        // 5. Atribut Khusus (Level Pedas / Ukuran)
        atributCard = new JPanel(new BorderLayout(0, 4));
        atributCard.setOpaque(false);
        lblAtribut = new JLabel("Level Pedas:");
        lblAtribut.setFont(UITheme.FONT_BOLD);

        cbAtribut = new JComboBox<>();
        cbAtribut.setFont(UITheme.FONT_REGULAR);
        cbAtribut.setBackground(Color.WHITE);
        updateAtributOptions(true);

        atributCard.add(lblAtribut, BorderLayout.NORTH);
        atributCard.add(cbAtribut, BorderLayout.CENTER);

        // Susun ke form
        form.add(lblKat);
        form.add(radioPanel);
        form.add(Box.createRigidArea(new Dimension(0, 10)));

        form.add(lblId);
        form.add(txtId);
        form.add(Box.createRigidArea(new Dimension(0, 10)));

        form.add(lblNama);
        form.add(txtNama);
        form.add(Box.createRigidArea(new Dimension(0, 10)));

        form.add(lblHarga);
        form.add(txtHarga);
        form.add(Box.createRigidArea(new Dimension(0, 10)));

        form.add(atributCard);

        add(form, BorderLayout.CENTER);

        // Footer Actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 16));
        footer.setBackground(UITheme.BG_MAIN);

        SecondaryButton btnBatal = new SecondaryButton("Batal", 10);
        btnBatal.setPreferredSize(new Dimension(90, 36));
        btnBatal.addActionListener(e -> dispose());

        PrimaryButton btnSimpan = new PrimaryButton("Simpan Menu", 10);
        btnSimpan.setPreferredSize(new Dimension(130, 36));
        btnSimpan.addActionListener(e -> simpanMenu());

        footer.add(btnBatal);
        footer.add(btnSimpan);

        add(footer, BorderLayout.SOUTH);
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(UITheme.FONT_REGULAR);
        tf.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
        tf.setPreferredSize(new Dimension(Short.MAX_VALUE, 36));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_DARK, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        return tf;
    }

    private void updateAtributOptions(boolean isMakanan) {
        cbAtribut.removeAllItems();
        if (isMakanan) {
            lblAtribut.setText("Level Pedas (Makanan):");
            cbAtribut.addItem("Biasa");
            cbAtribut.addItem("Sedang");
            cbAtribut.addItem("Pedas");
            cbAtribut.addItem("Ekstra Pedas");
        } else {
            lblAtribut.setText("Ukuran Minuman (S/M/L):");
            cbAtribut.addItem("S");
            cbAtribut.addItem("M");
            cbAtribut.addItem("L");
        }
    }

    private void populateData() {
        txtId.setText(itemToEdit.getId());
        txtId.setEnabled(false); // ID tidak boleh diedit jika sedang ubah
        txtNama.setText(itemToEdit.getNama());
        txtHarga.setText(String.valueOf((long) itemToEdit.getHarga()));

        if (itemToEdit instanceof Makanan) {
            rbMakanan.setSelected(true);
            updateAtributOptions(true);
            cbAtribut.setSelectedItem(((Makanan) itemToEdit).getTingkatPedas());
        } else if (itemToEdit instanceof Minuman) {
            rbMinuman.setSelected(true);
            updateAtributOptions(false);
            cbAtribut.setSelectedItem(((Minuman) itemToEdit).getUkuran());
        }
    }

    private void simpanMenu() {
        String id = txtId.getText().trim();
        String nama = txtNama.getText().trim();
        String hargaStr = txtHarga.getText().trim();

        if (id.isEmpty() || nama.isEmpty() || hargaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double harga;
        try {
            harga = Double.parseDouble(hargaStr);
            if (harga <= 0) {
                JOptionPane.showMessageDialog(this, "Harga harus lebih besar dari 0!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka yang valid!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String atribut = (String) cbAtribut.getSelectedItem();
        boolean isMakanan = rbMakanan.isSelected();

        try {
            MenuItem newItem = isMakanan
                    ? new Makanan(id, nama, harga, atribut)
                    : new Minuman(id, nama, harga, atribut);

            if (itemToEdit == null) {
                // Tambah baru
                menuManager.tambah(newItem);
            } else {
                // Ubah data
                menuManager.ubah(id, newItem);
            }

            dispose();
            if (callback != null) {
                callback.onSaved();
            }

        } catch (InputTidakValidException | MenuNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }
}
