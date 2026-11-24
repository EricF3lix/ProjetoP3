package repositories;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class GeradorPdfRelatorio {

    public void gerarPdf(File arquivoDestino, String tituloRelatorio, String dataEmissao, String[] cabecalhos, List<List<String>> linhas) throws IOException, DocumentException {
        try (FileOutputStream fos = new FileOutputStream(arquivoDestino);
             Document document = new Document()) {

            PdfWriter.getInstance(document, fos);
            document.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            document.add(new Paragraph(tituloRelatorio, fontTitulo));

            Font fontSub = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            document.add(new Paragraph("Gerado em: " + dataEmissao + " | Total de registros: " + linhas.size() + "\n\n", fontSub));

            if (cabecalhos != null && cabecalhos.length > 0) {
                PdfPTable tabelaPdf = new PdfPTable(cabecalhos.length);
                tabelaPdf.setWidthPercentage(100);

                Font fontCabecalho = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
                for (String col : cabecalhos) {
                    PdfPCell cell = new PdfPCell(new Phrase(col, fontCabecalho));
                    cell.setBackgroundColor(new Color(0, 122, 255));
                    cell.setPadding(6);
                    tabelaPdf.addCell(cell);
                }

                Font fontCorpo = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
                for (List<String> linha : linhas) {
                    for (String valor : linha) {
                        PdfPCell cell = new PdfPCell(new Phrase(valor != null ? valor : "", fontCorpo));
                        cell.setPadding(5);
                        tabelaPdf.addCell(cell);
                    }
                }

                document.add(tabelaPdf);
            }
        }
    }
}