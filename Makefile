
SUBDIRS := camel-main quarkus spring-boot

package:
	@$(foreach dir, $(SUBDIRS), $(MAKE) -C $(dir) package;)

clean:
	@$(foreach dir, $(SUBDIRS), $(MAKE) -C $(dir) clean;)
