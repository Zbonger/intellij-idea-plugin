/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.plugin.idea.ui.component;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.ui.TextFieldWithAutoCompletion;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.plugin.idea.util.IDEUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author Adam Wyłuda
 */
public class DirectoryChooserComponentBuilder extends ComponentBuilder
{
    @Override
    public ForgeComponent build(InputComponent<?, Object> input)
    {
        return new LabeledComponent(input, new ChooserComponent(context, input)
        {
            @Override
            public ActionListener createBrowseButtonActionListener(final TextFieldWithAutoCompletion textField)
            {
                return new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        String initialValue = textField.getText();
                        String value = IDEUtil.chooseFile(
                                context,
                                FileChooserDescriptorFactory.createSingleFolderDescriptor(),
                                initialValue);
                        if (value != null)
                        {
                            textField.setText(value);
                        }
                    }
                };
            }
        });
    }

    @Override
    protected Class<?> getProducedType()
    {
        return File.class;
    }

    @Override
    protected String getSupportedInputType()
    {
        return InputType.DIRECTORY_PICKER;
    }

    @Override
    protected Class<?>[] getSupportedInputComponentTypes()
    {
        return new Class<?>[]{UIInput.class};
    }
}
