
SUBDIRS := \
    bitcoin-source \
    kafka-oauth \
    platform-http-oauth \
    rest-oauth \
    timer-log

package:
	@$(foreach dir, $(SUBDIRS), $(MAKE) -C $(dir) package;)

clean:
	@$(foreach dir, $(SUBDIRS), $(MAKE) -C $(dir) clean;)
