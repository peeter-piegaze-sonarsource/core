
# Some remarks related to APIv2 and payload in step definitions :

# 1. There are several UPDATE request that do not do their job, such as User does not
#    update the fields description, code

# 2. Some other entities, such as TradingCurrency uses "non-normalized" fields such as
#    'prDescription' (instead of 'description'). How to deal with this problem???
#     A possible solution is to create a mapping between "non-normalized" fields
#     and "normalized" fields

# 3. How to deal with payload containing nested entities ???


# Some remarks related to generation process :

# 1. We need