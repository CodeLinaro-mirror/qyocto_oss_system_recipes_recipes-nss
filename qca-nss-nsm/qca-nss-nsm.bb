DESCRIPTION = "Kernel module qca-nss-nsm"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module
inherit systemd

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS_prepend := "${THISDIR}/:"

SRC_URI = "file://qca-nss-nsm \
	  "

DEPENDS_append += "virtual/kernel qca-nss-sfe qca-nss-ppe qca-nss-dp"

S = "${WORKDIR}/qca-nss-nsm"
SFE_STG_INCDIR = "${STAGING_INCDIR}/qca-nss-sfe"
PPE_STG_INCDIR = "${STAGING_INCDIR}/qca-nss-ppe"
DP_STG_INCDIR = "${STAGING_INCDIR}/qca-nss-dp"

PACKAGES += "kernel-module-nsm"

EXTRA_CFLAGS += "\
		-I${SFE_STG_INCDIR} \
		-I${PPE_STG_INCDIR} \
		-I${DP_STG_INCDIR} \
		"

#Using single qoutes to enacapsulate the path of Module.symvers
MODULE_EXTRA_SYMBOLS ="'${SFE_STG_INCDIR}/Module.symvers "
MODULE_EXTRA_SYMBOLS += "${PPE_STG_INCDIR}/Module.symvers ${DP_STG_INCDIR}/Module.symvers' "

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
	KBUILD_EXTRA_SYMBOLS=${MODULE_EXTRA_SYMBOLS} \
	modules
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 qca-nss-nsm${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-nss-nsm
	install -m 0644 exports/* ${D}${includedir}/qca-nss-nsm/
	install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-nsm/Module.symvers
}
