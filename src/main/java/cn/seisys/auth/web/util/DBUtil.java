package cn.seisys.auth.web.util;

import java.math.BigDecimal;

public class DBUtil
{

    public static Boolean convertBigDecimal2Boolean(Object value)
    {
        Boolean result = false;
        if (value != null)
        {
            if (value instanceof BigDecimal)
            {
                BigDecimal decValue = (BigDecimal) value;
                if (decValue.intValue() == 1)
                {
                    result = true;
                }
            }
            else if (value instanceof Boolean)
            {
                result = (Boolean) value;
            }
        }
        return result;
    }

    public static int convertBigDecimal2Integer(Object value)
    {
        int result = 0;
        if (value != null)
        {
            if (value instanceof BigDecimal)
            {
                BigDecimal decValue = (BigDecimal) value;
                result = decValue.intValue();
            }
        }
        return result;
    }
}