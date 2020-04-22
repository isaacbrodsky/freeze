package com.isaacbrodsky.zztsearch.query.indexing;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.isaacbrodsky.zztsearch.etl.text.BoardGameText;
import com.isaacbrodsky.zztsearch.etl.text.ElementGameText;
import com.isaacbrodsky.zztsearch.etl.text.GameTextVisitor;
import com.isaacbrodsky.zztsearch.etl.text.ObjectGameText;
import com.isaacbrodsky.zztsearch.etl.text.WorldGameText;
import lombok.Getter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;

import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.lucene.document.Field.Store;

/**
 * NOT thread safe
 *
 * Formats GameText into Lucene documents.
 */
class Formatter implements GameTextVisitor {
    private static final Joiner NEWLINE_JOINER = Joiner.on('\n');

    private static final Set<String> COMBINED_FIELDS = ImmutableSet.of(
            "world_name",
            "board_title",
            "board_message",
            "element_text",
            "object_name",
            "object_text"
    );

    @Getter
    private final Document doc;

    Formatter() {
        this.doc = new Document();
    }

    @Override
    public void visit(WorldGameText world) {
        doc.add(new StringField("world_name", world.getName(), Store.YES));
        doc.add(new StringField("world_patb", world.getPath(), Store.YES));
        doc.add(new StringField("world_worldname", world.getWorld(), Store.YES));
        doc.add(new StringField("world_mode", world.getMode().toString(), Store.YES));
        doc.add(new StringField("world_save", Boolean.toString(world.isSave()), Store.YES));
        doc.add(new TextField("world_flags", NEWLINE_JOINER.join(world.getFlags()), Store.YES));
    }

    @Override
    public void visit(BoardGameText board) {
        doc.add(new StringField("board_istitle", Boolean.toString(board.isTitle()), Store.YES));
        doc.add(new StringField("board_index", Integer.toString(board.getIndex()), Store.YES));
        doc.add(new TextField("board_title", board.getTitle(), Store.YES));
        doc.add(new TextField("board_message", board.getMessage(), Store.YES));
        doc.add(new StringField("board_visualhash", board.getVisualHash(), Store.YES));
        board.getWorld().accept(this);
    }

    @Override
    public void visit(ObjectGameText object) {
        if (object.getName() != null)
            doc.add(new TextField("object_name", object.getName(), Store.YES));
        doc.add(new TextField("object_text", NEWLINE_JOINER.join(object.getTexts()), Store.YES));
        doc.add(new StringField("object_scroll", Boolean.toString(object.isScroll()), Store.YES));
        doc.add(new TextField("object_comments", NEWLINE_JOINER.join(object.getComments()), Store.YES));
        doc.add(new TextField("object_labels", NEWLINE_JOINER.join(object.getLabels()), Store.YES));
        object.getBoard().accept(this);
    }

    @Override
    public void visit(ElementGameText element) {
        doc.add(new TextField("element_text", element.getText(), Store.YES));
        element.getBoard().accept(this);
    }

    /**
     * Combine the value of all fields into a new field
     * @param document
     */
    public static void addCombined(Document document) {
        final String combined = document.getFields().stream()
                .filter(f -> COMBINED_FIELDS.contains(f.name()))
                .map(IndexableField::stringValue)
                .collect(Collectors.joining("\n"));
        document.add(new TextField("combined", combined, Store.NO));
    }
}
