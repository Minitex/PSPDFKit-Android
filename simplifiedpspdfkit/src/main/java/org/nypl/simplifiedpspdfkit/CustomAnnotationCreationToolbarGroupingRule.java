package org.nypl.simplifiedpspdfkit;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.pspdfkit.ui.toolbar.ContextualToolbarMenuItem;
import com.pspdfkit.ui.toolbar.grouping.presets.MenuItem;
import com.pspdfkit.ui.toolbar.grouping.presets.PresetMenuItemGroupingRule;

import java.util.ArrayList;
import java.util.List;

public class CustomAnnotationCreationToolbarGroupingRule extends PresetMenuItemGroupingRule {

    // Underline and highlight annotations
    // Initial capacity is required to be at least 3
    private static final List<MenuItem> CUSTOM_GROUP = new ArrayList<>(4);

    static {
        CUSTOM_GROUP.add(new MenuItem(com.pspdfkit.R.id.pspdf__annotation_creation_toolbar_item_underline));
        CUSTOM_GROUP.add(new MenuItem(com.pspdfkit.R.id.pspdf__annotation_creation_toolbar_item_highlight));
        // toolbar_item_picker is what allows users to choose the color of the underline/highlight annotation
        CUSTOM_GROUP.add(new MenuItem(com.pspdfkit.R.id.pspdf__annotation_creation_toolbar_item_picker));
        CUSTOM_GROUP.add(new MenuItem(com.pspdfkit.R.id.pspdf__annotation_creation_toolbar_item_undo_redo, new int[] {
                com.pspdfkit.R.id.pspdf__annotation_creation_toolbar_item_undo,
                com.pspdfkit.R.id.pspdf__annotation_creation_toolbar_item_redo,
        }));
    }

    public CustomAnnotationCreationToolbarGroupingRule(@NonNull Context context) {
        super(context);
    }

    @Override
    @NonNull
    public List<ContextualToolbarMenuItem> groupMenuItems(@NonNull List<ContextualToolbarMenuItem> flatItems, @IntRange(from = 4) int capacity) {
        // Here we can set certain menu items to be justified to the left (start) or right(end) of
        // the toolbar.
        for (ContextualToolbarMenuItem item : flatItems) {
            if (item.getId() == com.pspdfkit.R.id.pspdf__annotation_creation_toolbar_item_undo_redo) {
                item.setPosition(ContextualToolbarMenuItem.Position.END);
            } else {
                item.setPosition(ContextualToolbarMenuItem.Position.START);
            }
        }

        return super.groupMenuItems(flatItems, capacity);
    }

    @Override
    public List<MenuItem> getGroupPreset(int capacity, int itemCount) {
        final List<MenuItem> presetGrouping;

        // Here is where you could set different groupings based on capacity.
        presetGrouping = CUSTOM_GROUP;
        return presetGrouping;
    }
}
