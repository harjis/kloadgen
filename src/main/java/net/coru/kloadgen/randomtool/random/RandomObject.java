/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.coru.kloadgen.randomtool.random;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.github.curiousoddman.rgxgen.RgxGen;
import net.coru.kloadgen.exception.KLoadGenException;
import net.coru.kloadgen.model.ConstraintTypeEnum;
import net.coru.kloadgen.randomtool.util.ValidTypeConstants;
import net.coru.kloadgen.randomtool.util.ValueUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

public class RandomObject {

  public boolean isTypeValid(String type) {
    return ValidTypeConstants.VALID_OBJECT_TYPES.contains(type);
  }

  public Object generateSeq(String fieldName, String fieldType, List<String> fieldValueList, Map<String, Object> context) {
    return context.compute(fieldName, (fieldNameMap, seqObject) ->
        seqObject == null
            ? getFirstValueOrDefaultForType(fieldValueList, fieldType)
            : addOneCasted(seqObject, fieldType));
  }

  public Object generateSequenceForFieldValueList(String fieldName, String fieldType, List<String> fieldValueList, Map<String, Object> context) {
    Integer index = (Integer) context.compute(fieldName, (fieldNameMap, seqObject) -> seqObject == null ? 0 : (((Integer) seqObject) + 1) % fieldValueList.size());
    return ValueUtils.castValue(fieldValueList.get(index), fieldType);
  }

  public Object generateRandom(
      String fieldType, Integer valueLength, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    Object value;
    switch (fieldType.toLowerCase()) {
      case ValidTypeConstants.STRING:
        value = getStringValueOrRandom(valueLength, fieldValueList, constrains);
        break;
      case ValidTypeConstants.INT:
        try {
          value = getIntegerValueOrRandom(valueLength, fieldValueList, constrains).intValueExact();
        } catch (ArithmeticException exception) {
          value = Integer.MAX_VALUE;
        }
        break;
      case ValidTypeConstants.LONG:
        try {
          value = getIntegerValueOrRandom(valueLength, fieldValueList, constrains).longValueExact();
        } catch (ArithmeticException exception) {
          value = Long.MAX_VALUE;
        }
        break;
      case ValidTypeConstants.SHORT:
        try {
          value = getIntegerValueOrRandom(valueLength, fieldValueList, constrains).shortValueExact();
        } catch (ArithmeticException exception) {
          value = Short.MAX_VALUE;
        }
        break;
      case ValidTypeConstants.DOUBLE:
        try {
          value = getDecimalValueOrRandom(valueLength, fieldValueList, constrains).doubleValue();
        } catch (ArithmeticException exception) {
          value = Double.MAX_VALUE;
        }
        break;
      case ValidTypeConstants.NUMBER:
      case ValidTypeConstants.FLOAT:
        try {
          value = getDecimalValueOrRandom(valueLength, fieldValueList, constrains).floatValue();
        } catch (ArithmeticException exception) {
          value = Float.MAX_VALUE;
        }
        break;
      case ValidTypeConstants.BYTES:
        try {
          value = getIntegerValueOrRandom(valueLength, Collections.emptyList(), Collections.emptyMap()).byteValueExact();
        } catch (ArithmeticException exception) {
          value = Byte.MAX_VALUE;
        }
        break;
      case ValidTypeConstants.TIMESTAMP:
      case ValidTypeConstants.LONG_TIMESTAMP:
      case ValidTypeConstants.STRING_TIMESTAMP:
        value = getTimestampValueOrRandom(fieldType, fieldValueList);
        break;
      case ValidTypeConstants.UUID:
        value = getUUIDValueOrRandom(fieldValueList);
        break;
      case ValidTypeConstants.BOOLEAN:
        value = getBooleanValueOrRandom(fieldValueList);
        break;
      case ValidTypeConstants.ENUM:
        value = getEnumValueOrRandom(fieldValueList);
        break;
      case ValidTypeConstants.INT_DATE:
        value = getDateValueOrRandom(fieldValueList);
        break;
      case ValidTypeConstants.INT_TIME_MILLIS:
        value = getTimeMillisValueOrRandom(fieldValueList);
        break;
      case ValidTypeConstants.LONG_TIME_MICROS:
        value = getTimeMicrosValueOrRandom(fieldValueList);
        break;
      case ValidTypeConstants.LONG_TIMESTAMP_MILLIS:
        value = getTimestampMillisValueOrRandom(fieldValueList);
        break;
      case ValidTypeConstants.LONG_TIMESTAMP_MICROS:
        value = getTimestampMicrosValueOrRandom(fieldValueList);
        break;
      case ValidTypeConstants.LONG_LOCAL_TIMESTAMP_MILLIS:
        value = getLocalTimestampMillisValueOrRandom(fieldValueList);
        break;
      case ValidTypeConstants.LONG_LOCAL_TIMESTAMP_MICROS:
        value = getLocalTimestampMicrosValueOrRandom(fieldValueList);
        break;
      case ValidTypeConstants.STRING_UUID:
        value = getUUIDValueOrRandom(fieldValueList);
        break;
      case ValidTypeConstants.BYTES_DECIMAL:
        value = getDecimalValueOrRandom(fieldValueList, constrains);
        break;
      case ValidTypeConstants.FIXED_DECIMAL:
        value = getDecimalValueOrRandom(fieldValueList, constrains);
        break;
      default:
        value = fieldType;
        break;
    }

    return value;
  }

  private Object getFirstValueOrDefaultForType(List<String> fieldValueList, String fieldType) {
    if (!fieldValueList.isEmpty()) {
      return ValueUtils.castValue(fieldValueList.get(0), fieldType);
    }

    switch (fieldType) {
      case ValidTypeConstants.INT:
        return 1;
      case ValidTypeConstants.DOUBLE:
        return 1.0;
      case ValidTypeConstants.LONG:
        return 1L;
      case ValidTypeConstants.FLOAT:
        return 1.0f;
      case ValidTypeConstants.SHORT:
        return (short) 1;
      case ValidTypeConstants.BYTES_DECIMAL:
      case ValidTypeConstants.FIXED_DECIMAL:
      default:
        return BigDecimal.ONE;
    }
  }

  private Object addOneCasted(Object seqObject, String fieldType) {
    Object castValue;
    switch (fieldType) {
      case ValidTypeConstants.INT:
        castValue = Integer.parseInt(seqObject.toString()) + 1;
        break;
      case ValidTypeConstants.DOUBLE:
        castValue = Double.parseDouble(seqObject.toString()) + 1;
        break;
      case ValidTypeConstants.LONG:
        castValue = Long.parseLong(seqObject.toString()) + 1;
        break;
      case ValidTypeConstants.FLOAT:
        castValue = Float.parseFloat(seqObject.toString()) + 1;
        break;
      case ValidTypeConstants.SHORT:
        castValue = Short.parseShort(seqObject.toString()) + 1;
        break;
      case ValidTypeConstants.BYTES_DECIMAL:
      case ValidTypeConstants.FIXED_DECIMAL:
      default:
        castValue = new BigDecimal(seqObject.toString()).add(BigDecimal.ONE);
        break;
    }
    return castValue;
  }

  private BigInteger getIntegerValueOrRandom(Integer valueLength, List<String> fieldValueList, Map<ConstraintTypeEnum, String> constrains) {
    BigInteger value;

    if (!fieldValueList.isEmpty()) {
      value = new BigInteger(fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim());
    } else {
      Number minimum = calculateMinimum(valueLength, constrains);
      Number maximum = calculateMaximum(valueLength, constrains);

      if (constrains.containsKey(ConstraintTypeEnum.MULTIPLE_OF)) {
        int multipleOf = Integer.parseInt(constrains.get(ConstraintTypeEnum.MULTIPLE_OF));
        maximum = maximum.intValue() > multipleOf ? maximum.intValue() / multipleOf : maximum;
        value = BigInteger.valueOf(RandomUtils.nextLong(minimum.longValue(), maximum.longValue()) * multipleOf);
      } else {
        value = BigInteger.valueOf(RandomUtils.nextLong(minimum.longValue(), maximum.longValue()));
      }
    }

    return value;
  }

  private BigDecimal getDecimalValueOrRandom(Integer valueLength, List<String> fieldValueList, Map<ConstraintTypeEnum, String> constrains) {
    BigDecimal value;

    if (!fieldValueList.isEmpty()) {
      value = new BigDecimal(fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim());
    } else {
      Number minimum = calculateMinimum(valueLength - 1, constrains);
      Number maximum = calculateMaximum(valueLength - 1, constrains);

      if (constrains.containsKey(ConstraintTypeEnum.MULTIPLE_OF)) {
        int multipleOf = Integer.parseInt(constrains.get(ConstraintTypeEnum.MULTIPLE_OF));
        maximum = maximum.intValue() > multipleOf ? maximum.intValue() / multipleOf : maximum;
        value = BigDecimal.valueOf(RandomUtils.nextDouble(minimum.doubleValue(), maximum.doubleValue()) * multipleOf);
      } else {
        if (valueLength < 3) {
          value = new BigDecimal(getIntegerValueOrRandom(valueLength, fieldValueList, constrains));
        } else {
          BigDecimal aux = BigDecimal.valueOf(RandomUtils.nextLong(minimum.longValue(), maximum.longValue()));
          int decLength = RandomUtils.nextInt(1, valueLength / 2);
          value = aux.multiply(BigDecimal.valueOf(0.1).pow(decLength));
        }
      }
    }

    return value;
  }

  private String getStringValueOrRandom(
      Integer valueLength, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    String value;
    if (!fieldValueList.isEmpty() && !StringUtils.isEmpty(fieldValueList.get(0))) {
      value = fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim();
    } else {
      if (constrains.containsKey(ConstraintTypeEnum.REGEX)) {
        RgxGen rxGenerator = new RgxGen(constrains.get(ConstraintTypeEnum.REGEX));
        value = rxGenerator.generate();
        if (valueLength > 0 || constrains.containsKey(ConstraintTypeEnum.MAXIMUM_VALUE)) {
          value = value.substring(0, getMaxLength(valueLength, constrains.get(ConstraintTypeEnum.MAXIMUM_VALUE)));
        }
      } else {
        value = RandomStringUtils.randomAlphabetic(valueLength == 0 ? RandomUtils.nextInt(1, 20) : valueLength);
      }
    }
    return value;
  }

  private int getMaxLength(Integer valueLength, String maxValueStr) {
    int maxValue = Integer.parseInt(StringUtils.defaultIfEmpty(maxValueStr, "0"));
    if (valueLength > 0 && maxValue == 0) {
      maxValue = valueLength;
    }
    return maxValue;
  }

  private Object getTimestampValueOrRandom(String type, List<String> fieldValueList) {
    LocalDateTime value;
    if (!fieldValueList.isEmpty()) {
      value = LocalDateTime.parse(fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim());
    } else {
      value = LocalDateTime.now();
    }
    if ("longTimestamp".equalsIgnoreCase(type)) {
      return value.toInstant(ZoneOffset.UTC).toEpochMilli();
    } else if ("stringTimestamp".equalsIgnoreCase(type)) {
      return value.toString();
    }
    return value;
  }

  private UUID getUUIDValueOrRandom(List<String> fieldValueList) {
    UUID value = UUID.randomUUID();
    if (!fieldValueList.isEmpty()) {
      value = UUID.fromString(fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim());
    }
    return value;
  }

  private Boolean getBooleanValueOrRandom(List<String> fieldValueList) {
    boolean value = RandomUtils.nextBoolean();
    if (!fieldValueList.isEmpty()) {
      value = Boolean.parseBoolean(fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim());
    }
    return value;
  }

  private String getEnumValueOrRandom(List<String> fieldValueList) {
    String value;
    if (!fieldValueList.isEmpty()) {
      value = fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim();
    } else {
      throw new KLoadGenException("Wrong enums values, problem in the parsing process");
    }
    return value;
  }

  private Number calculateMaximum(int valueLength, Map<ConstraintTypeEnum, String> constrains) {
    Number maximum;
    if (constrains.containsKey(ConstraintTypeEnum.MAXIMUM_VALUE)) {
      if (constrains.containsKey(ConstraintTypeEnum.EXCLUDED_MAXIMUM_VALUE)) {
        maximum = Long.parseLong(constrains.get(ConstraintTypeEnum.EXCLUDED_MAXIMUM_VALUE)) - 1L;
      } else {
        maximum = Long.parseLong(constrains.get(ConstraintTypeEnum.MAXIMUM_VALUE));
      }
    } else {
      maximum = new BigDecimal(StringUtils.rightPad("9", valueLength, '0'));
    }
    return maximum;
  }

  private Number calculateMinimum(int valueLength, Map<ConstraintTypeEnum, String> constrains) {
    Number minimum;
    if (constrains.containsKey(ConstraintTypeEnum.MINIMUM_VALUE)) {
      if (constrains.containsKey(ConstraintTypeEnum.EXCLUDED_MINIMUM_VALUE)) {
        minimum = Long.parseLong(constrains.get(ConstraintTypeEnum.EXCLUDED_MINIMUM_VALUE)) - 1;
      } else {
        minimum = Long.parseLong(constrains.get(ConstraintTypeEnum.MINIMUM_VALUE));
      }
    } else {
      minimum = Long.parseLong(StringUtils.rightPad("1", valueLength, '0'));
    }
    return minimum;
  }

  private static LocalDate getDateValueOrRandom(List<String> fieldValueList) {
    LocalDate resultDate;
    int minDay = (int) LocalDate.of(1900, 1, 1).toEpochDay();
    int maxDay = (int) LocalDate.of(2100, 1, 1).toEpochDay();
    long randomDay = minDay + RandomUtils.nextInt(0, maxDay - minDay);
    if (fieldValueList.isEmpty()) {
      resultDate = LocalDate.ofEpochDay(randomDay);
    } else {
      resultDate = LocalDate.parse(fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim());
    }
    return resultDate;
  }

  private static LocalTime getRandomLocalTime(List<String> fieldValueList) {
    long nanoMin = 0;
    long nanoMax = 24L * 60L * 60L * 1_000_000_000L - 1L;
    if (fieldValueList.isEmpty()) {
      return LocalTime.ofNanoOfDay(RandomUtils.nextLong(nanoMin, nanoMax));
    } else {
      return LocalTime.parse(fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim());
    }
  }

  private static LocalTime getTimeMillisValueOrRandom(List<String> fieldValueList) {
    return getRandomLocalTime(fieldValueList);
  }

  private static LocalTime getTimeMicrosValueOrRandom(List<String> fieldValueList) {
    return getRandomLocalTime(fieldValueList);
  }

  private static LocalDateTime getRandomLocalDateTime(List<String> fieldValueList) {
    long minDay = LocalDateTime.of(1900, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);
    long maxDay = LocalDateTime.of(2100, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);
    long randomSeconds = minDay + RandomUtils.nextLong(0, maxDay - minDay);

    if (fieldValueList.isEmpty()) {
      return LocalDateTime.ofEpochSecond(randomSeconds, RandomUtils.nextInt(0, 1_000_000_000 - 1), ZoneOffset.UTC);
    } else {
      return LocalDateTime.parse(fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim());
    }
  }

  private static Instant getTimestampMillisValueOrRandom(List<String> fieldValueList) {
    return getRandomLocalDateTime(fieldValueList).toInstant(ZoneOffset.UTC);
  }

  private static Instant getTimestampMicrosValueOrRandom(List<String> fieldValueList) {
    return getRandomLocalDateTime(fieldValueList).toInstant(ZoneOffset.UTC);
  }

  private static LocalDateTime getLocalTimestampMillisValueOrRandom(List<String> fieldValueList) {
    return getRandomLocalDateTime(fieldValueList);
  }

  private static LocalDateTime getLocalTimestampMicrosValueOrRandom(List<String> fieldValueList) {
    return getRandomLocalDateTime(fieldValueList);
  }

  private static long randomNumberWithLength(int n) {
    long min = (long) Math.pow(10, n - 1);
    return RandomUtils.nextLong(min, min * 10);
  }

  private static BigDecimal getDecimalValueOrRandom(
      List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    int scale;
    int precision;

    if (Objects.nonNull(constrains.get(ConstraintTypeEnum.PRECISION))) {
      precision = Integer.parseInt(constrains.get(ConstraintTypeEnum.PRECISION));
      scale = Objects.nonNull(constrains.get(ConstraintTypeEnum.SCALE)) ?
          Integer.parseInt(constrains.get(ConstraintTypeEnum.SCALE)) : 0;

      if (precision <= 0) {
        throw new KLoadGenException("Decimal precision must be greater dan 0");
      }
      if (scale < 0 || scale > precision) {
        throw new KLoadGenException("Scale must be zero or a positive integer less than or equal to the precision");
      }

      if (fieldValueList.isEmpty()) {
        return BigDecimal.valueOf(randomNumberWithLength(precision), scale);
      } else {
        return new BigDecimal(fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim());
      }

    } else {
      throw new KLoadGenException("Missing decimal precision");
    }
  }

}
