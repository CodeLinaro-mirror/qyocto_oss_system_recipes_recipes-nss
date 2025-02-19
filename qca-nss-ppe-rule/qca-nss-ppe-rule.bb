DESCRIPTION = "Kernel module for NSS PPE-RULE driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

CLEANBROKEN = "1"

SOC = "${@d.getVar('SOC_FAMILY', d, 1).split(':')[1]}"
SOC_TYPE = "${@d.getVar('SOC', d, 0).split('_')[0]}"

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/:"

SRC_URI = "file://qca-nss-ppe \
	   file://files \
	   "
PACKAGES += "kernel-module-qca-nss-ppe-rule"

DEPENDS = "virtual/kernel qca-nss-ppe qca-nss-ppe-vp"

S = "${WORKDIR}/qca-nss-ppe/drv/ppe_rule"

PPE_RULE_MAKE_OPTS:${SOC} = "ppe-rule=y \
		PPE_RFS_ENABLED=y \
		PPE_ACL_ENABLED=y \
		PPE_POLICER_ENABLED=y \
		PPE_PRIORITY_ENABLED=y \
		PPE_MIRROR_ENABLED=y \
		PPE_QOS_ENABLED=y \
		"
PPE_RULE_MAKE_OPTS:ipq53xx:append = "PPE_RULE_IPQ53XX=y"
PPE_RULE_MAKE_OPTS:ipq53xx_64:append = "PPE_RULE_IPQ53XX=y"
PPE_RULE_MAKE_OPTS:ipq54xx:append = "PPE_RULE_IPQ54XX=y"
PPE_RULE_MAKE_OPTS:ipq54xx_64:append = "PPE_RULE_IPQ54XX=y"

EXTRA_CFLAGS += " \
		-I${STAGING_INCDIR}/qca-ssdk \
		-I${STAGING_INCDIR}/qca-ssdk/fal \
		-I${STAGING_INCDIR}/qca-ssdk/init \
		"

MODULE_EXTRA_SYMBOLS = "${STAGING_INCDIR}/qca-nss-ppe/Module.symvers \
			${STAGING_INCDIR}/qca-nss-ppe-vp/Module.symvers \
			"

do_configure() {
	true
}

do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" ${PPE_RULE_MAKE_OPTS} \
	CROSS_COMPILE="${TARGET_PREFIX}" \
	ARCH="${KARCH}" \
	M="${S}" \
	EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
	KBUILD_EXTRA_SYMBOLS="${MODULE_EXTRA_SYMBOLS}" \
	SoC='${SOC_TYPE}' \
	modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/qca-nss-ppe-rule${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-ppe-rule
	install -m 0644 ${WORKDIR}/qca-nss-ppe/drv/exports/* ${D}${includedir}/qca-nss-ppe-rule/
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-ppe-rule/Module.symvers
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/files/acl_dump.sh ${D}${bindir}/acl_dump.sh
	install -m 0755 ${WORKDIR}/files/json_mcast_hammer.sh ${D}${bindir}/json_mcast_hammer.sh
	install -m 0755 ${WORKDIR}/files/mcast_hammer.sh ${D}${bindir}/mcast_hammer.sh
}

FILES:${PN} = "${bindir}/acl_dump.sh \
	${bindir}/json_mcast_hammer.sh \
	${bindir}/mcast_hammer.sh \
	"

KERNEL_MODULE_AUTOLOAD += "qca-nss-ppe-rule"
