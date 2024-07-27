package liqp.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import liqp.Template;
import liqp.TemplateContext;
import liqp.TemplateParser;

public class Strip_HTMLTest {

    @Test
    public void applyTest() throws RecognitionException {

        String json = "{ \"html\" : \"1<h>2</h>3\" }";

        String[][] tests = {
                {"{{ nil | strip_html }}", ""},
                {"{{ 456 | strip_html }}", "456"},
                {"{{ '45<6' | strip_html }}", "45<6"},
                {"{{ '<a>' | strip_html }}", ""},
                {"{{ html | strip_html }}", "123"},
        };

        for (String[] test : tests) {

            Template template = TemplateParser.DEFAULT.parse(test[0]);
            String rendered = String.valueOf(template.render(json));

            assertThat(rendered, is(test[1]));
        }
    }

    /*
     * def test_strip_html
     *   assert_equal 'test', @filters.strip_html("<div>test</div>")
     *   assert_equal 'test', @filters.strip_html("<div id='test'>test</div>")
     *   assert_equal '', @filters.strip_html("<script type='text/javascript'>document.write('some stuff');</script>")
     *   assert_equal '', @filters.strip_html(nil)
     * end
     */
    @Test
    public void applyOriginalTest() {
        TemplateContext context = new TemplateContext();
        Filter filter = Filters.COMMON_FILTERS.get("strip_html");

        assertThat(filter.apply("<div>test</div>", context), is((Object)"test"));
        assertThat(filter.apply("<div id='test'>test</div>", context), is((Object)"test"));
        assertThat(filter.apply("<script type='text/javascript'>document.write('some stuff');</script>", context), is((Object)""));
        assertThat(filter.apply(null, context), is((Object)""));
    }


    @Test
    public void testIssue306() {
        // given
        // {{ "<em>test</em>" | escape }} --> &lt;em&gt;test&lt;/em&gt;
        TemplateContext context = new TemplateContext();
        Filter filter = Filters.COMMON_FILTERS.get("strip_html");

        // when
        Object result = filter.apply("&lt;em&gt;test&lt;/em&gt;", context);


        // then
        assertThat(result, is("&lt;em&gt;test&lt;/em&gt;"));
    }
}
