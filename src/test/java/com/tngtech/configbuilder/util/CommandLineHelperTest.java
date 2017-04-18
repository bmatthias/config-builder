package com.tngtech.configbuilder.util;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.tngtech.configbuilder.annotation.valueextractor.CommandLineValue;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.ConfigBuilderException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommandLineHelperTest {

    private static class TestConfig {
        @CommandLineValue(shortOpt = "u", longOpt = "user", required = true)
        public String aString;
        @CommandLineValue(shortOpt = "v", longOpt = "vir", required = false)
        public String anotherString;
    }

    private CommandLineHelper commandLineHelper;
    private String[] args = null;

    @Mock
    private Options options;
    @Mock
    private GnuParser parser;
    @Mock
    private CommandLine commandLine;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private AnnotationHelper annotationHelper;
    @Mock
    private ErrorMessageSetup errorMessageSetup;

    @Before
    public void setUp() throws Exception {
        when(configBuilderFactory.getInstance(AnnotationHelper.class)).thenReturn(annotationHelper);
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(errorMessageSetup);

        commandLineHelper = new CommandLineHelper(configBuilderFactory);

        Set<Field> fields = newHashSet(TestConfig.class.getDeclaredFields());
        when(annotationHelper.getFieldsAnnotatedWith(TestConfig.class, CommandLineValue.class)).thenReturn(fields);
        when(parser.parse(options, args)).thenReturn(commandLine);
    }

    @Test
    public void testGetCommandLine() throws Exception {
        when(configBuilderFactory.createInstance(GnuParser.class)).thenReturn(parser);
        when(configBuilderFactory.createInstance(Options.class)).thenReturn(options);
        ArgumentCaptor<Option> captor = ArgumentCaptor.forClass(Option.class);
        assertThat(commandLineHelper.getCommandLine(TestConfig.class, args)).isSameAs(commandLine);
        verify(options, times(2)).addOption(captor.capture());
        verify(parser).parse(options, args);
        List<Option> options = captor.getAllValues();

        assertThat(options).hasSize(2);

        final ImmutableList<Option> sortedOptions = FluentIterable.from(options).toSortedList(new Comparator<Option>() {
            @Override
            public int compare(Option o1, Option o2) {
                return o1.getLongOpt().compareTo(o2.getLongOpt());
            }
        });

        assertThat(sortedOptions.get(0).getLongOpt()).isEqualTo("user");
        assertThat(sortedOptions.get(0).getOpt()).isEqualTo("u");
        assertThat(sortedOptions.get(0).isRequired()).isEqualTo(true);

        assertThat(sortedOptions.get(1).getLongOpt()).isEqualTo("vir");
        assertThat(sortedOptions.get(1).getOpt()).isEqualTo("v");
        assertThat(sortedOptions.get(1).isRequired()).isEqualTo(false);
    }

    @Test(expected = ConfigBuilderException.class)
    public void testGetCommandLineThrowsException() {
        when(configBuilderFactory.createInstance(GnuParser.class)).thenReturn(new GnuParser());
        when(configBuilderFactory.createInstance(Options.class)).thenReturn(new Options());
        args = new String[]{"nd", "notDefined"};
        commandLineHelper.getCommandLine(TestConfig.class, args);
    }

    @Test
    public void testGetOptions() {
        Options options1 = new Options();
        when(configBuilderFactory.createInstance(Options.class)).thenReturn(options1);
        assertThat(commandLineHelper.getOptions(TestConfig.class)).isEqualTo(options1);
        assertThat(options1.getOption("user").getLongOpt()).isEqualTo("user");
        assertThat(options1.getOption("vir").getOpt()).isEqualTo("v");
    }
}
