package view.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.function.Consumer;
import java.util.regex.Pattern;

public class IntListener implements ChangeListener<String> {

  private static final Pattern INTEGER_PATTERN = Pattern.compile("\\d*");

  private Consumer<Integer> consumer;

  public IntListener(Consumer<Integer> consumer) {
    this.consumer = consumer;
  }

  @Override
  public void changed(ObservableValue<? extends String> c, String o, String n) {
    if(n.length() > 0 && INTEGER_PATTERN.matcher(n).matches()) {
      consumer.accept(Integer.parseInt(n));
    }
  }
}
