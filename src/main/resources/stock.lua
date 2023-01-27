if (redis.call("exists", KEYS[1]) == 1) then
    if (redis.call("exists", KEYS[2]) == 0) then
        local stock = tonumber(redis.call("get", KEYS[1]));
        if (stock > 0) then
            redis.call("incrby", KEYS[1], -1);
            redis.call("set", KEYS[2], 1);
            redis.call("pexpire", KEYS[2], 100);
            return stock;
        end;
            return -1;
    end;
        return -2;
end;