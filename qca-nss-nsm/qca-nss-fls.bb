DESCRIPTION = "Flow Statistics"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module
inherit systemd

CLEANBROKEN = "1"

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/:"

SRC_URI = "file://qca-nss-nsm \
          "

DEPENDS:append += "virtual/kernel qca-nss-sfe qca-nss-ecm"

S = "${WORKDIR}/qca-nss-nsm/fls"

PACKAGES += "kernel-module-fls"

FLS_MAKE_OPTS += "FLS_ECM_CLASSIFIER_EMESH_ENABLE=y"

EXTRA_CFLAGS += "\
		-I${STAGING_INCDIR}/qca-nss-sfe \
		-I${STAGING_INCDIR}/qca-nss-ecm \
		"
MODULE_EXTRA_SYMBOLS += "\
			${STAGING_INCDIR}/qca-nss-sfe/Module.symvers \
			${STAGING_INCDIR}/qca-nss-ecm/Module.symvers \
			"

do_configure() {
	true
}

do_compile() {
	unset LDFLAGS
	make -C "${STAGING_KERNEL_BUILDDIR}" \
	CROSS_COMPILE="${TARGET_PREFIX}" \
	ARCH='${KARCH}' \
	M="${S}" \
	EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
	KBUILD_EXTRA_SYMBOLS="${MODULE_EXTRA_SYMBOLS}" \
	${FLS_MAKE_OPTS} \
	modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ${S}/qca-nss-fls${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-fls
	install -m 0644 ${S}/*.h ${D}${includedir}/qca-nss-fls/
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-fls/Module.symvers
}
