package net.coru.kloadgen.extractor.parser.proto;

import static net.coru.kloadgen.extractor.parser.proto.OptionElement.formatOptionList;
import static net.coru.kloadgen.extractor.parser.proto.Utils.appendDocumentation;
import static net.coru.kloadgen.extractor.parser.proto.Utils.checkNotNull;
import static net.coru.kloadgen.extractor.parser.proto.Utils.immutableCopyOf;

import com.google.auto.value.AutoValue;
import java.util.ArrayList;
import java.util.List;

@AutoValue
abstract class EnumConstantElement {
  public static Builder builder() {
    return new Builder();
  }

  EnumConstantElement() {
  }

  public abstract String name();
  public abstract int tag();
  public abstract String documentation();
  public abstract List<OptionElement> options();

  public final String toSchema() {
    StringBuilder builder = new StringBuilder();
    appendDocumentation(builder, documentation());
    builder.append(name())
        .append(" = ")
        .append(tag());
    if (!options().isEmpty()) {
      builder.append(" [\n");
      formatOptionList(builder, options());
      builder.append(']');
    }
    return builder.append(";\n").toString();
  }

  public static final class Builder {
    private String name;
    private Integer tag;
    private String documentation = "";
    private final List<OptionElement> options = new ArrayList<>();

    private Builder() {
    }

    public Builder name(String name) {
      this.name = checkNotNull(name, "name");
      return this;
    }

    public Builder tag(int tag) {
      this.tag = tag;
      return this;
    }

    public Builder documentation(String documentation) {
      this.documentation = checkNotNull(documentation, "documentation");
      return this;
    }

    public Builder addOption(OptionElement option) {
      options.add(checkNotNull(option, "option"));
      return this;
    }

    public EnumConstantElement build() {
      checkNotNull(name, "name");
      checkNotNull(tag, "tag");

      return new AutoValue_EnumConstantElement(name, tag, documentation, immutableCopyOf(options));
    }
  }
}
